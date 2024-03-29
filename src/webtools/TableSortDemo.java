/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableSortDemo extends JFrame{
  protected JTable table = new JTable();

  protected MyTableModel tableModel;

  protected JLabel titleLabel = new JLabel("Click table header to sort the column.");

  public TableSortDemo() {
    super();
    setSize(600, 300);

    tableModel = new MyTableModel();

    getContentPane().add(titleLabel, BorderLayout.NORTH);
    table.setModel(tableModel);

    JTableHeader header = table.getTableHeader();
    header.setUpdateTableInRealTime(true);
    header.addMouseListener(tableModel.new ColumnListener(table));
    header.setReorderingAllowed(true);

    JScrollPane ps = new JScrollPane();
    ps.getViewport().add(table);
    getContentPane().add(ps, BorderLayout.CENTER);

    WindowListener wndCloser = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    };
    addWindowListener(wndCloser);
    setVisible(true);
  }

  public static void main(String argv[]) {
    new TableSortDemo();
  }
}
class MyTableModel extends AbstractTableModel {
  protected int sortCol = 0;

  protected boolean isSortAsc = true;

  protected int m_result = 0;
  protected int columnsCount = 1;
  Vector vector = new Vector();
  public MyTableModel() {
    vector.removeAllElements();
    vector.addElement(new Integer(24976600));
    vector.addElement(new Integer(24));
    vector.addElement(new Integer(2497));
    vector.addElement(new Integer(249766));
    vector.addElement(new Integer(2497660));
    vector.addElement(new Integer(6600));
    vector.addElement(new Integer(76600));
    vector.addElement(new Integer(976600));
    vector.addElement(new Integer(4976600));
  }

  public int getRowCount() {
    return vector == null ? 0 : vector.size();
  }

  public int getColumnCount() {
    return columnsCount;
  }

  public String getColumnName(int column) {
    String str = "data";
    if (column == sortCol)
      str += isSortAsc ? " >>" : " <<";
    return str;
  }

  public boolean isCellEditable(int nRow, int nCol) {
    return false;
  }

  public Object getValueAt(int nRow, int nCol) {
    if (nRow < 0 || nRow >= getRowCount())
      return "";
    if(nCol>1){
      return "";
    }
    return vector.elementAt(nRow);
  }

  public String getTitle() {
    return "data ";
  }

  class ColumnListener extends MouseAdapter {
    protected JTable table;

    public ColumnListener(JTable t) {
      table = t;
    }

    public void mouseClicked(MouseEvent e) {
      TableColumnModel colModel = table.getColumnModel();
      int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
      int modelIndex = colModel.getColumn(columnModelIndex)
          .getModelIndex();

      if (modelIndex < 0)
        return;
      if (sortCol == modelIndex)
        isSortAsc = !isSortAsc;
      else
        sortCol = modelIndex;

      for (int i = 0; i < columnsCount; i++) { 
        TableColumn column = colModel.getColumn(i);
        column.setHeaderValue(getColumnName(column.getModelIndex()));
      }
      table.getTableHeader().repaint();

      Collections.sort(vector,new MyComparator(isSortAsc));
      table.tableChanged(new TableModelEvent(MyTableModel.this));
      table.repaint();
    }
  }
}

class MyComparator implements Comparator {
  protected boolean isSortAsc;

  public MyComparator( boolean sortAsc) {
    isSortAsc = sortAsc;
  }

  public int compare(Object o1, Object o2) {
    if (!(o1 instanceof Integer) || !(o2 instanceof Integer))
      return 0;
    Integer s1 = (Integer) o1;
    Integer s2 = (Integer) o2;
    int result = 0;
    result = s1.compareTo(s2);
    if (!isSortAsc)
      result = -result;
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof MyComparator) {
      MyComparator compObj = (MyComparator) obj;
      return compObj.isSortAsc == isSortAsc;
    }
    return false;
  }
}
