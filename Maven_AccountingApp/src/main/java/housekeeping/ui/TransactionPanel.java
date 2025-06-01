package housekeeping.ui;

import housekeeping.ImagePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Date;

public class TransactionPanel extends ImagePanel {
	    private static final long serialVersionUID = 1L;
	    public JTextField nameInput;
	    public JComboBox<String> typeInput;
	    public JTextField amountInput;
	    public JTextArea noteInput;
	    public JSpinner dateInput;
	    public JSpinner timeInput;
	    public JButton submitButton;
	    public JButton sumBtn;

	    public TransactionPanel(Image image,
	                             String[] categoryList,
	                             ActionListener onSubmit,
	                             ActionListener onBackToSummary) {
	        super(image);
	        setLayout(null);
	        
	        
	        sumBtn = new JButton("");
	        sumBtn.setIcon(new ImageIcon(getClass().getResource("/checkexpense.jpg")));
	        sumBtn.setBorder(null);
	        sumBtn.setBounds(29, 123, 259, 40);
	        sumBtn.addActionListener(onBackToSummary);
	        add(sumBtn);

	        JLabel lblName = new JLabel("Name");
	        lblName.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        lblName.setBounds(378, 123, 139, 49);
	        add(lblName);

	        JLabel lblType = new JLabel("Type");
	        lblType.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        lblType.setBounds(378, 203, 139, 49);
	        add(lblType);

	        JLabel lblAmount = new JLabel("Amount");
	        lblAmount.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        lblAmount.setBounds(378, 284, 139, 49);
	        add(lblAmount);

	        JLabel lblNote = new JLabel("Note");
	        lblNote.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        lblNote.setBounds(378, 370, 139, 49);
	        add(lblNote);

	        nameInput = new JTextField();
	        nameInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        nameInput.setBounds(527, 123, 935, 49);
	        add(nameInput);
	        nameInput.setColumns(10);

	        typeInput = new JComboBox<>(categoryList);
	        typeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        typeInput.setBounds(527, 203, 935, 49);
	        typeInput.setBackground(Color.WHITE);
	        add(typeInput);
	        
	        System.out.println("categoryList = " + Arrays.toString(categoryList));

	        amountInput = new JTextField();
	        amountInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        amountInput.setBounds(527, 284, 935, 49);
	        add(amountInput);
	        amountInput.setColumns(10);

	        noteInput = new JTextArea();
	        noteInput.setFont(new Font("Courier New", Font.PLAIN, 33));
	        noteInput.setBounds(527, 370, 935, 60);
	        noteInput.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	        add(noteInput);

	        JLabel lblDate = new JLabel("Date");
	        lblDate.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        lblDate.setBounds(378, 490, 139, 49);
	        add(lblDate);

	        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
	        dateInput = new JSpinner(dateModel);
	        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateInput, "yyyy-MM-dd");
	        dateInput.setEditor(dateEditor);
	        dateInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        dateInput.setBounds(527, 490, 300, 49);
	        add(dateInput);

	        JLabel lblTime = new JLabel("Time");
	        lblTime.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        lblTime.setBounds(378, 550, 139, 49);
	        add(lblTime);

	        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
	        timeInput = new JSpinner(timeModel);
	        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeInput, "HH:mm:ss");
	        timeInput.setEditor(timeEditor);
	        timeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        timeInput.setBounds(527, 550, 300, 49);
	        add(timeInput);

	        submitButton = new JButton("SUBMIT");
	        submitButton.setFont(new Font("Tahoma", Font.PLAIN, 33));
	        submitButton.setBounds(527, 620, 935, 71);
	        submitButton.addActionListener(onSubmit);
	        add(submitButton);
	    }

}
