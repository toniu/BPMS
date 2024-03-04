/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badmintonSystem;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Neka
 */
public class theModel extends AbstractTableModel {
    
    // declaration of private variables
    private String[] columns;
    private Object[][] rows;
    
    public theModel(){}
    
    // use of constructor
    public theModel(Object[][] data, String[] columnName){
    
        this.rows = data;
        this.columns = columnName;
    }

    // returns the column number
    public Class getColumn(int column){
    // 3 is the index of the column image
        if(column == 3){
            return Icon.class;
        }
        else {
            return getValueAt(0,column).getClass();
        }
    }
    
    // returns the row count of the table
    public int getRowCount() {
     return this.rows.length;
    }
    
    // returns the column count of the table
    public int getColumnCount() {
     return this.columns.length;
    }

    // returns the value at the specific row index, column index in the parameters
    public Object getValueAt(int rowIndex, int columnIndex) {
    
    return this.rows[rowIndex][columnIndex];
    }
    
    // returns the name of the column
    public String getColumnName(int col){
        return this.columns[col];
    }


}