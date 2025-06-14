package housekeeping.ui;

import housekeeping.ImagePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel extends ImagePanel {
	private static final long serialVersionUID = 1L;
    public JTextField idField;
    public JPasswordField passField;
    public JCheckBox rememberMe;
    public JButton logInBtn;
    public JButton signupBtn;

    public LoginPanel(Image image, ActionListener onLogin, ActionListener onSignup) {
        super(image);
        setLayout(null);
        
        idField = new JTextField();
        idField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        idField.setBounds(1223, 311, 296, 43);
        idField.setBackground(new Color(234, 226, 219));
        idField.setSize(315, 38);
        idField.setLocation(660, 326);
        idField.setBorder(null);
        add(idField);

        passField = new JPasswordField();
        passField.setFont(new Font("Tahoma", Font.PLAIN, 26));
        passField.setBounds(1223, 391, 296, 43);
        passField.setBackground(new Color(234, 226, 219));
        passField.setSize(315, 38);
        passField.setLocation(660, 410);
        passField.setBorder(null);
        add(passField);

        rememberMe = new JCheckBox();
        rememberMe.setBounds(1184, 440, 25, 25);
        rememberMe.setLocation(577, 493);
        add(rememberMe);

        logInBtn = new JButton("");
        logInBtn.setBounds(1183, 467, 338, 38);
        logInBtn.setIcon(new ImageIcon(getClass().getResource("/loginbtn.jpg"))); 
        logInBtn.setPressedIcon(new ImageIcon(getClass().getResource("/loginbtn.jpg")));
        logInBtn.setSize(377, 38);
        logInBtn.setLocation(608, 480);
        logInBtn.setBorder(null);
        logInBtn.addActionListener(onLogin);
        add(logInBtn);

        signupBtn = new JButton("회원가입");
        signupBtn.setBackground(new Color(234, 226, 219));
        signupBtn.setBounds(1183, 520, 338, 38);
        signupBtn.setSize(376, 38);
        signupBtn.setLocation(608, 543);
        signupBtn.addActionListener(onSignup);
        add(signupBtn);
    }

    public String getUsername() {
        return idField.getText();
    }

    public String getPassword() {
        return new String(passField.getPassword());
    }
}
