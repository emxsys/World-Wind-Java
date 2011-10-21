/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.symbology.AbstractIconRetriever;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.awt.image.*;

/**
 * @author ccrick
 * @version $Id: MilStd2525IconRetriever.java 90 2011-17-10 23:58:29Z ccrick $
 */
public class MilStd2525IconRetriever extends AbstractIconRetriever
{
    // TODO: add more error checking

    public BufferedImage createIcon(String symbolIdentifier, AVList params)
    {
        // retrieve desired symbol and convert to bufferedImage
        SymbolCode symbolCode = new SymbolCode(symbolIdentifier);

        BufferedImage img = null;
        String filename = getFilename(symbolCode);

        if (params.getValue(SymbolCode.SOURCE_TYPE).equals("file"))
        {
            String path = (String) params.getValue(SymbolCode.SOURCE_PATH);

            img = retrieveImageFromFile(path, filename, img);
        }
        else if (params.getValue(SymbolCode.SOURCE_TYPE).equals("url"))
        {
            String server = (String) params.getValue(SymbolCode.SOURCE_SERVER);
            String path = (String) params.getValue(SymbolCode.SOURCE_PATH);

            img = retrieveImageFromURL(server, path, filename, img);
        }

        if (img == null)
        {
            return null;
        }

        // apply dotted border where required by Standard Identity (cases P, A, S, G, M)
        String stdid = (String) symbolCode.getValue(SymbolCode.STANDARD_IDENTITY);
        if ("PASGMpasgm".indexOf(stdid.charAt(0)) > -1)
        {
            BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dest.createGraphics();
            g.setComposite(AlphaComposite.SrcOver);
            g.setBackground(new Color(0, 0, 0, 0));
            g.clearRect(0, 0, dest.getWidth(), dest.getHeight());
            g.drawImage(img, 0, 0, null);
            // now overlay dotted line
            BufferedImage overlay = retrieveOverlay(symbolCode, params);
            if (overlay != null)
                g.drawImage(overlay, 0, 0, null);
            g.dispose();

            img = dest;
        }

        // TODO: modify image with given params

        return img;
    }

    protected BufferedImage retrieveOverlay(SymbolCode symbolCode, AVList params)
    {
        BufferedImage img = null;
        String filename = null;
        String stdID = (String) symbolCode.getValue(SymbolCode.STANDARD_IDENTITY);
        String battleDim = (String) symbolCode.getValue(SymbolCode.BATTLE_DIMENSION);
        String functionID = (String) symbolCode.getValue(SymbolCode.FUNCTION_ID);

        if (stdID.equalsIgnoreCase(SymbolCode.IDENTITY_PENDING) ||
            stdID.equalsIgnoreCase(SymbolCode.IDENTITY_EXERCISE_PENDING))
        {
            if (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_UNKNOWN) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_GROUND) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SEA_SURFACE) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SOF))
            {
                // 1. clover
                filename = "clover_overlay.png";
            }
            else if (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SPACE) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_AIR))
            {
                // 2. cloverTop
                filename = "clovertop_overlay.png";
            }
            else if (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SUBSURFACE))
            {
                // 3. cloverBottom
                filename = "cloverbottom_overlay.png";
            }
        }
        else if
            (stdID.equalsIgnoreCase(SymbolCode.IDENTITY_ASSUMED_FRIEND) ||
                stdID.equalsIgnoreCase(SymbolCode.IDENTITY_EXERCISE_ASSUMED_FRIEND))
        {
            if (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_UNKNOWN) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SEA_SURFACE))
            {
                // 4. circle
                filename = "circle_overlay.png";
            }
            else if
                (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SPACE) ||
                    battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_AIR))
            {
                // 5. arch
                filename = "arch_overlay.png";
            }
            else if (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SUBSURFACE))
            {
                // 6. smile
                filename = "smile_overlay.png";
            }
            else if
                (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_GROUND) ||
                    battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SOF))
            {

                if ("G".equalsIgnoreCase(functionID.substring(0, 1)))   // special case of Ground Equipment
                {
                    // 4. circle
                    filename = "circle_overlay.png";
                }
                else        // Units and Installations
                {
                    // 7. rectangle
                    filename = "rectangle_overlay.png";
                }
            }
        }
        else if (stdID.equalsIgnoreCase(SymbolCode.IDENTITY_SUSPECT))
        {
            if (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_UNKNOWN) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_GROUND) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SEA_SURFACE) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SOF))
            {
                // 8. diamond
                filename = "diamond_overlay.png";
            }
            else if (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SPACE) ||
                battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_AIR))
            {
                // 9. tent
                filename = "tent_overlay.png";
            }
            else if (battleDim.equalsIgnoreCase(SymbolCode.BATTLE_DIMENSION_SUBSURFACE))
            {
                // 10. top
                filename = "top_overlay.png";
            }
        }

        if (params.getValue(SymbolCode.SOURCE_TYPE).equals("file"))
        {
            String path = (String) params.getValue(SymbolCode.SOURCE_PATH);

            img = retrieveImageFromFile(path, filename, img);
        }
        else if (params.getValue(SymbolCode.SOURCE_TYPE).equals("url"))
        {
            String server = (String) params.getValue(SymbolCode.SOURCE_SERVER);
            String path = (String) params.getValue(SymbolCode.SOURCE_PATH);

            img = retrieveImageFromURL(server, path, filename, img);
        }

        return img;
    }

    protected static String getFilename(SymbolCode code)
    {

        String standardID = (String) code.getValue(SymbolCode.STANDARD_IDENTITY);
        standardID = standardID.toLowerCase();

        int prefix = 0;
        char stdid = 'u';
        // See Table I and TABLE II, p.15 of MIL-STD-2525C for standard identities that use similar shapes
        switch (standardID.charAt(0))
        {
            case 'p':      // PENDING
            case 'u':      // UNKNOWN
            case 'g':      // EXERCISE PENDING
            case 'w':      // EXERCISE UNKNOWN
                prefix = 0;
                stdid = 'u';
                break;
            case 'f':      // FRIEND
            case 'a':      // ASSUMED FRIEND
            case 'd':      // EXERCISE FRIEND
            case 'm':      // EXERCISE ASSUMED FRIEND
            case 'j':      // JOKER
            case 'k':      // FAKER
                prefix = 1;
                stdid = 'f';
                break;
            case 'n':      // NEUTRAL
            case 'l':      // EXERCISE NEUTRAL
                prefix = 2;
                stdid = 'n';
                break;
            case 'h':      // HOSTILE
            case 's':      // SUSPECT
                prefix = 3;
                stdid = 'h';
                break;
            default:
                String msg = Logging.getMessage("Symbology.InvalidSymbolCode", standardID);
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
        }

        String padding = "-----";

        String result = Integer.toString(prefix) + '.' + code.getValue(SymbolCode.SCHEME).toString().toLowerCase()
            + stdid + code.getValue(SymbolCode.BATTLE_DIMENSION).toString().toLowerCase()
            + 'p' + code.getValue(SymbolCode.FUNCTION_ID).toString().toLowerCase()
            //+ code.getValue(SymbolCode.SYMBOL_MODIFIER).toString().toLowerCase()
            + padding + ".png";
        return result;
    }
}
