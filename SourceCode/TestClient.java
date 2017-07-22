package Test;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.markdown4j.Markdown4jProcessor;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.CORBA.StringHolder;

import net.oschina.md2.export.FileFactory;

class User {
	private String name;

	public User(String n) {
		if (n.contains("<h1>")) {
			n = n.replace("<h1>", "");
			n = n.replace("</h1>", "");
		} else if (n.contains("<h2>")) {
			n = n.replace("<h2>", "");
			n = n.replace("</h2>", "");
		} else if (n.contains("<h3>")) {
			n = n.replace("<h3>", "");
			n = n.replace("</h3>", "");
		} else if (n.contains("<h4>")) {
			n = n.replace("<h4>", "");
			n = n.replace("</h4>", "");
		} else if (n.contains("<h5>")) {
			n = n.replace("<h5>", "");
			n = n.replace("</h5>", "");
		} else if (n.contains("<h6>")) {
			n = n.replace("<h6>", "");
			n = n.replace("</h6>", "");
		}
		name = n;
	}

	// 重点在toString，节点的显示文本就是toString
	public String toString() {
		return name;
	}
}

public class TestClient {
	private JFrame frame = new JFrame("MarkdownEditor");
	private TextArea textarea_unedit = new TextArea("");
	private TextArea textarea_edit = new TextArea("");
	private JTextArea textarea_head = new JTextArea("");
	private JMenuBar menubar = new JMenuBar();
	private FileDialog openDia, saveDia;
	private File file;
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<DefaultMutableTreeNode> list_tree = new ArrayList<DefaultMutableTreeNode>();
	private ArrayList<Integer> index = new ArrayList<Integer>();
	private String head = new String();
	private String d = new String();
	private String d1 = new String();
	private String s = new String();
	private String s1 = new String();
	private boolean IsConnect = false;
	private int flag = 0, flag1 = 0;
	private JTextPane jeditpane = new JTextPane();
	private JScrollPane scrollPane = new JScrollPane(jeditpane);
	private JPanel jPanel_Html = new JPanel(new BorderLayout(0, 5));
	private JPanel jPanel1_unedit = new JPanel(new BorderLayout(0, 5));
	private JPanel jPanel2_edit = new JPanel(new BorderLayout(0, 5));
	private JPanel jPanel3_head = new JPanel(new BorderLayout(0, 5));
	private Dialog dialog;
	private DefaultMutableTreeNode top = new DefaultMutableTreeNode("Structure");
	private DefaultTreeModel defaultTreeModel = new DefaultTreeModel(top);
	private JTree tree = new JTree(defaultTreeModel);
	private JScrollPane scrollPane1 = new JScrollPane(tree);
	
	public static final int PORT = 8080;
	private InetAddress addr;
	private Socket socket;
	private  BufferedReader in;
	private PrintWriter out;
	
	class ClientThread extends Thread{
		 public void run(){
			 try{
				 IsConnect = true;
				 //addr = InetAddress.getByName("10.111.230.183");
				 addr = InetAddress.getLocalHost() ;
					socket = new Socket(addr, PORT);
					JOptionPane
					.showMessageDialog(null,
							"Connect Successful",
							null, JOptionPane.INFORMATION_MESSAGE);
					 in =  new BufferedReader(
					            new InputStreamReader(
					              socket.getInputStream()));
					 out =  new PrintWriter(
					            new BufferedWriter(
					              new OutputStreamWriter(
					                socket.getOutputStream())),true);
					 while(true){
						 String line = "";
						 String result = "";
						 while (!(line = in.readLine()).equals("pos")) {
							 result += (line+"\n");
							}
						 result = result.substring(0,result.length()-1);
						 textarea_edit.setText(result);
						 line = in.readLine();				 
						 Integer pos = Integer.parseInt(line.replaceAll("\n", ""));
						 textarea_edit.setCaretPosition(pos);
					 }
			 }
			 catch(IOException ee){
				 IsConnect = false;
				 JOptionPane
				 .showMessageDialog(null,
							"Connect failed",
							null, JOptionPane.INFORMATION_MESSAGE);
			 }
		 }
	}
	

	// 创建窗口，并初始化
	private void makeframe() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setFont(new Font("宋体", Font.BOLD, 15));
		JMenu help = new JMenu("Help");
		help.setFont(new Font("宋体", Font.BOLD, 15));
		JMenu Connection = new JMenu("Connect Setting");
		menubar.add(fileMenu);
		menubar.add(help);
		menubar.add(Connection);
		JMenuItem help1 = new JMenuItem("结构说明");
		help.add(help1);
		JMenuItem Connect = new JMenuItem("Connect");
		Connection.add(Connect);
		JMenuItem DISConnect = new JMenuItem("DisConnect");
		Connection.add(DISConnect);
		JMenuItem openItem = new JMenuItem("Open txt");
		fileMenu.add(openItem);
		JMenuItem opencssItem = new JMenuItem("Add css");
		fileMenu.add(opencssItem);
		JMenuItem SaveItem = new JMenuItem("Generate docx");
		fileMenu.add(SaveItem);
		JMenuItem SaveItem1 = new JMenuItem("Generate HTML");
		fileMenu.add(SaveItem1);
		JMenuItem CloseItem = new JMenuItem("Close");
		fileMenu.add(CloseItem);
		frame.setJMenuBar(menubar);
		textarea_edit.setFont(new Font("黑体", Font.BOLD, 15));
		textarea_unedit.setFont(new Font("黑体", Font.BOLD, 15));
		frame.setLayout(new GridLayout(1, 3));
		frame.setLocation(200, 50);
		frame.setSize(800, 600);
		jPanel_Html.add(new JLabel("HTML:"), BorderLayout.NORTH);
		jPanel_Html.add(scrollPane);
		jPanel1_unedit.add(new JLabel("HTML Script:"), BorderLayout.NORTH);
		jPanel1_unedit.add(textarea_unedit);
		jPanel2_edit.add(new JLabel("Editor Area:"), BorderLayout.NORTH);
		jPanel2_edit.add(textarea_edit);
		jPanel3_head.add(new JLabel("Head Structure:"), BorderLayout.NORTH);
		jPanel3_head.add(scrollPane1);

		frame.add(jPanel3_head);
		frame.add(jPanel1_unedit);
		frame.add(jPanel2_edit);
		frame.add(jPanel_Html);

		openDia = new FileDialog(frame, "我的打开", FileDialog.LOAD);
		saveDia = new FileDialog(frame, "我的保存", FileDialog.SAVE);

		// 设置MenuItem的监听
		help1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(null,
								"\n" + "最左边第一栏是抽取出来的标题，点击每一个标题能使第三栏光标跳转到对应标题的第一行,\n" + "第二栏是HTML源码,\n"
										+ "第三栏是编辑栏，即用户输入栏,\n" + "最后一栏是HTML网页.\n",
								"结构说明", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		DISConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IsConnect = false;
				JOptionPane
				.showMessageDialog(null,
						"DisConnect Successful",
						null, JOptionPane.INFORMATION_MESSAGE);
				try{
					socket.close();
					}
					catch (Exception IOE) {
						// TODO: handle exception
					}
			}
		});
		
		Connect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ClientThread Client = new ClientThread();
	
				Client.start();
				
				textarea_edit.addKeyListener(new KeyListener() {
					
					@Override
					public void keyTyped(KeyEvent arg0) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub
						if(IsConnect){
						Integer pos = textarea_edit.getCaretPosition();
						String string = textarea_edit.getText().toString();
						string += "\npos";
						string += ("\n" + pos.toString());
						System.out.println(string);
						out.println(string);
						}
					}
					
					@Override
					public void keyPressed(KeyEvent arg0) {
						// TODO Auto-generated method stub
					}
				});
				}
		});
		openItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				openDia.setVisible(true);
				String dirPath = openDia.getDirectory();// 获取文件路径
				String fileName = openDia.getFile();// 获取文件名称

				if (dirPath == null || fileName == null)
					return;

				textarea_edit.setText("");

				file = new File(dirPath, fileName);

				try {
					BufferedReader bufr = new BufferedReader(new FileReader(file));

					String line = null;

					while ((line = bufr.readLine()) != null) {
						textarea_edit.append(line + "\r\n");
					}
					bufr.close();
				} catch (IOException ex) {
					throw new RuntimeException("文件读取失败！");
				}

			}
		});

		opencssItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				openDia.setVisible(true);
				String dirPath = openDia.getDirectory();// 获取文件路径
				String fileName = openDia.getFile();// 获取文件名称

				if (dirPath == null || fileName == null)
					return;

				file = new File(dirPath, fileName);

				try {
					BufferedReader bufr = new BufferedReader(new FileReader(file));

					String line = null;

					while ((line = bufr.readLine()) != null) {
						textarea_edit.append(line + "\r\n");
					}
					bufr.close();
				} catch (IOException ex) {
					throw new RuntimeException("文件读取失败！");
				}

			}
		});

		SaveItem.addActionListener(new ActionListener() {
			// 设置保存文件的功能
			public void actionPerformed(ActionEvent e) {
				saveDia.setVisible(true);
				String dirPath = saveDia.getDirectory();
				String fileName = saveDia.getFile();

				if (dirPath == null || fileName == null)
					return;
				file = new File(dirPath, "temp.md");

				try {
					BufferedWriter bufw = new BufferedWriter(new FileWriter(file));

					String text = textarea_edit.getText();

					bufw.write(text);

					bufw.close();
				} catch (IOException ex) {
					throw new RuntimeException("文件保存失败！");
				}

				try {
					// 导出文本
					FileFactory.produce(file, dirPath + fileName);
					// FileFactory.produce(new File("test_file/md_for_test.md"),
					// "test_file/test.pdf");
					// FileFactory.produce(new File("test_file/md_for_test.md"),
					// "test_file/test.html");
				} catch (FileNotFoundException et) {
					et.printStackTrace();
				}
			}
		});

		SaveItem1.addActionListener(new ActionListener() {
			// 设置保存文件的功能
			public void actionPerformed(ActionEvent e) {
				saveDia.setVisible(true);
				String dirPath = saveDia.getDirectory();
				String fileName = saveDia.getFile();

				if (dirPath == null || fileName == null)
					return;
				file = new File(dirPath, fileName);

				try {
					BufferedWriter bufw = new BufferedWriter(new FileWriter(file));

					String text = textarea_unedit.getText();

					bufw.write(text);

					bufw.close();
				} catch (IOException ex) {
					throw new RuntimeException("文件保存失败！");
				}

				try {
					FileFactory.produce(file, fileName);
				} catch (FileNotFoundException et) {
					et.printStackTrace();
				}
			}
		});

		CloseItem.addActionListener(new ActionListener() {
			// 设置退出功能
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		textarea_unedit.setEditable(false);
		textarea_unedit.setFocusable(false);
		jeditpane.setEditable(false);
		jeditpane.setFocusable(false);

		textarea_head.setEditable(false);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {  
			public void windowClosing(WindowEvent e) {  
			super.windowClosing(e);  
			//加入动作  
			// 
			try{
			socket.close();
			}
			catch (Exception IOE) {
				// TODO: handle exception
			}
			}
			  
			});   
	}

	// 用户输入触发监听事件
	private void edit() {

		textarea_edit.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				tree.clearSelection();
				s = textarea_edit.getText();
				
				list.clear();
				list_tree.clear();
				index.clear();
				head = "";
				
				try {
					d = new Markdown4jProcessor().process(s);
					textarea_unedit.setText(d);

					String result = null;
					Pattern p = Pattern
							.compile("<h1>.*</h1>|<h2>.*</h2>|<h3>.*</h3>|<h4>.*</h4>|<h5>.*</h5>|<h6>.*</h6>");
					Matcher m = p.matcher(d);
					while (m.find()) {
						result = m.group(0);
						list.add(result);
						index.add(m.start(0));
					}

					updateTree();


					for (int i = 0; i < list.size(); i++) {
						head += list.get(i);
						head += "\n";
					}
					textarea_head.setText(head);
					textarea_head.setFont(new Font("黑体", Font.BOLD, 20));

					/*
					 * String fileame = "1" + ".html"; FileOutputStream
					 * fileoutputstream = new FileOutputStream(fileame); byte
					 * tag_bytes[] = textarea_unedit.getText().getBytes();
					 * fileoutputstream.write(tag_bytes);
					 * fileoutputstream.close();
					 */

					jeditpane.setContentType("text/html");
					jeditpane.setText(textarea_unedit.getText());

				} catch (Exception ee) {
				}
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

				if (node == null)
					return;

				Object object = node.getUserObject();
				User user = (User) object;

				String line = "";
				int linenum = -1;
				int length = 0;
				BufferedReader br1 = new BufferedReader(new StringReader(s));
				try {
					while ((line = br1.readLine()) != null) {
						linenum++;
						int x = line.length();
						if (line.contains("#")) {
							line = line.replace("#", "");
							line = line.trim();

							if (line.equals(user.toString())) {

								break;
							}
						}

						else if (nextline(linenum + 1).equals("=") || nextline(linenum + 1).equals("-")) {
							line = line.trim();

							if (line.equals(user.toString())) {

								break;
							}
						}
						length += (x + 1);

					}
				} catch (IOException eeeee) {
					// TODO Auto-generated catch block
					eeeee.printStackTrace();
				}

				textarea_edit.requestFocus();
				textarea_edit.setCaretPosition(length);

			}
		});
		
	
	}
			
	private int Checklevel(String n) {
		int Level = 0;
		if (n.contains("<h1>")) {
			Level = 1;
		} else if (n.contains("<h2>")) {
			Level = 2;
		} else if (n.contains("<h3>")) {
			Level = 3;
		} else if (n.contains("<h4>")) {
			Level = 4;
		} else if (n.contains("<h5>")) {
			Level = 5;
		} else if (n.contains("<h6>")) {
			Level = 6;
		}
		return Level;
	}

	private String nextline(int linenum) {
		String line = "";
		int linecnt = -1;
		BufferedReader br1 = new BufferedReader(new StringReader(textarea_edit.getText()));
		try {
			while ((line = br1.readLine()) != null) {
				linecnt++;
				if (linecnt == linenum)
					break;
			}
		} catch (IOException eeeee) {
			// TODO Auto-generated catch block
			eeeee.printStackTrace();
		}
		return line;
	}

	public void updateTree() {
		Vector<TreePath> v = new Vector<TreePath>();
		getExpandNode(top, v);
		top.removeAllChildren();

		if (!list.isEmpty()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new User(list.get(0)));
			top.add(node);
			list_tree.add(node);
		}

		for (int i = 1; i < list.size(); i++) {
			int j = i - 1;
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new User(list.get(i)));
			for (; j >= 0; j--) {
				if (Checklevel(list.get(j)) < Checklevel(list.get(i))) {
					list_tree.get(j).add(node);
					list_tree.add(node);
					break;
				}
			}
			if (j == -1) {
				top.add(node);
				list_tree.add(node);
			}
		}
		defaultTreeModel.reload();

		int n = v.size();
		for (int i = 0; i < n; i++) {
			Object[] objArr = v.get(i).getPath();
			Vector<Object> vec = new Vector<Object>();
			int len = objArr.length;
			for (int j = 0; j < len; j++) {
				vec.add(objArr[j]);
			}
			expandNode(tree, top, vec);
		}
	}

	public Vector<TreePath> getExpandNode(TreeNode node, Vector<TreePath> v) {
		if (node.getChildCount() > 0) {
			TreePath treePath = new TreePath(defaultTreeModel.getPathToRoot(node));
			if (tree.isExpanded(treePath))
				v.add(treePath);
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				getExpandNode(n, v);
			}
		}
		return v;
	}

	void expandNode(JTree myTree, DefaultMutableTreeNode currNode, Vector<Object> vNode) {
		if (currNode.getParent() == null) {
			vNode.removeElementAt(0);
		}
		if (vNode.size() <= 0)
			return;

		int childCount = currNode.getChildCount();
		String strNode = vNode.elementAt(0).toString();
		DefaultMutableTreeNode child = null;
		boolean flag = false;
		for (int i = 0; i < childCount; i++) {
			child = (DefaultMutableTreeNode) currNode.getChildAt(i);
			if (strNode.equals(child.toString())) {
				flag = true;
				break;
			}
		}
		if (child != null && flag) {
			vNode.removeElementAt(0);
			if (vNode.size() > 0) {
				expandNode(myTree, child, vNode);
			} else {
				myTree.expandPath(new TreePath(child.getPath()));
			}
		}
	}
	
	/*private void MarkDownServer() throws IOException{
		addr = InetAddress.getByName(null);
		socket = new Socket(addr, PORT);
		 in =  new BufferedReader(
		            new InputStreamReader(
		              socket.getInputStream()));
		 out =  new PrintWriter(
		            new BufferedWriter(
		              new OutputStreamWriter(
		                socket.getOutputStream())),true);
		 while(true){
			 String line = "";
			 String result = "";
			 while (!(line = in.readLine()).equals("end")) {
				 result += (line+"\n");
				}
			 textarea_edit.setText(result);
		 }
	}*/

	public static void main(String[] args) throws Exception {
		TestClient editor = new TestClient();
		editor.makeframe();
		editor.edit();
	}
}
