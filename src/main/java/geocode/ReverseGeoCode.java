/*
The MIT License (MIT)
[OSI Approved License]
The MIT License (MIT)

Copyright (c) 2014 Daniel Glasson

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package geocode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import geocode.kdtree.KDTree;

/**
 *
 * Created by Daniel Glasson on 18/05/2014.
 * Uses KD-trees to quickly find the nearest point
 * 
 * ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream("c:\\AU.txt"), Arrays.asList('A');
 * System.out.println("Nearest to -23.456, 123.456 is " + geocode.nearestPlace(-23.456, 123.456));
 */
public class ReverseGeoCode {
    KDTree<GeoName> kdTree;
    
    /**
     * Parse the raw text geonames file.
     * @param placenames the text file downloaded from http://download.geonames.org/export/dump/; can not be null.
     * @param featureClasses Feature classes to use as locations in the geolocation. See http://www.geonames.org/export/codes.html
     * 
     * @throws IOException if there is a problem reading the stream.
     * @throws NullPointerException if zippedPlacenames is {@code null}.
     */
    public ReverseGeoCode( List<InputStream> placenames, List<Character> featureClasses) throws IOException {
        createKdTree(placenames, featureClasses);
    }
    
    private void createKdTree(List<InputStream> placenames, List<Character> featureClasses)
            throws IOException {
        
        // Prevent duplicate locations. This could be handled KDTree as well,
        // but this is faster to implement. 
        Set<Long> handled = new HashSet<>();
        
        Set<Character> featureClassSet = new HashSet<>(featureClasses);
        
        ArrayList<GeoName> arPlaceNames = new ArrayList<GeoName>();
        
        // Read the geonames file in the directory
        for (InputStream stream: placenames) {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String str;
            try {
                while ((str = in.readLine()) != null) {
                    GeoName newPlace = new GeoName(str);
                    if (featureClassSet.contains(newPlace.featureClass) && !handled.contains(newPlace.id)) {
                        arPlaceNames.add(newPlace);
                        handled.add(newPlace.id);
                    }
                }
            } catch (IOException ex) {
                throw ex;
            }finally{
                in.close();
            }
        }
        kdTree = new KDTree<GeoName>(arPlaceNames);
    }

    public GeoName nearestPlace(double latitude, double longitude) {
        return kdTree.findNearest(new GeoName(latitude,longitude));
    }
    
}
