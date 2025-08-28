package com.xyz.channelling;

import javax.swing.*;
import java.awt.*;

public class DoctorPanel extends JPanel {
    public DoctorPanel() {
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Doctor ID:"));
        JTextField idField = new JTextField();
        add(idField);

        add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        add(nameField);

        add(new JLabel("Specialization:"));
        JTextField specField = new JTextField();
        add(specField);

        JButton submit = new JButton("Register Doctor");
        add(submit);

        submit.addActionListener(__ -> {
            String name = nameField.getText();
            String spec = specField.getText();
            JOptionPane.showMessageDialog(this, "Doctor " + name + " (" + spec + ") Registered!");
        });
    }
}
