package uk.co.crider.jablus.utils;

import javax.swing.*;
import java.util.*;
 
public class UIManagerList {
 
    public static void main(String[] args) {
        UIDefaults defaults = UIManager.getDefaults();
        System.out.println(defaults.size()+ " properties deffined !");
        String[ ] colName = {"Key", "Value"};
        String[ ][ ] rowData = new String[ defaults.size() ][ 2 ];
        int i = 0;
        for(Enumeration e = defaults.keys(); e.hasMoreElements(); i++){
            Object key = e.nextElement();
            rowData[ i ] [ 0 ] = key.toString();
            rowData[ i ] [ 1 ] = ""+defaults.get(key);
            System.out.println(rowData[i][0]+" ,, "+rowData[i][1]);
        }
        JFrame f = new JFrame("UIManager properties default values");
        JTable t = new JTable(rowData, colName);
        f.setContentPane(new JScrollPane(t));
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
}