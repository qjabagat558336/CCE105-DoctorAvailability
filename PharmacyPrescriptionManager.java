package Pharmacy;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Doctor {
    private String name;
    private String specialization;
    private boolean isAvailable;

    public Doctor(String name, String specialization, boolean isAvailable) {
        this.name = name;
        this.specialization = specialization;
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getDisplayName() {
        return "Dr. " + name + " (" + specialization + ")";
    }
}

class Prescription {
    private String patientName;
    private String medication;
    private String prescribedBy;

    public Prescription(String patientName, String medication, String prescribedBy) {
        this.patientName = patientName;
        this.medication = medication;
        this.prescribedBy = prescribedBy;
    }

    public String getDetails() {
        return "Patient: " + patientName + ", Medication: " + medication + ", Prescribed by: " + prescribedBy;
    }
}

public class PharmacyPrescriptionManager extends JFrame {

    private List<Doctor> doctors = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();

    private JComboBox<String> doctorComboBox;
    private JTextField patientField;
    private JTextField medicationField;
    private JTextArea outputArea;

    public PharmacyPrescriptionManager() {
        setTitle("Pharmacy Prescription Manager (Admin)");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initDoctors();
        initUI();
    }

    private void initDoctors() {
        doctors.add(new Doctor("Bob Johnson", "Dermatologist", true));        // Not available
        doctors.add(new Doctor("Alice Smith", "Cardiologist", true));          // Available
        doctors.add(new Doctor("Cathy Lee", "General Practitioner", true));    // Available
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set background color of the main panel
        panel.setBackground(new Color(255, 255, 204)); // Light yellow background

        // Doctor selection (horizontal)
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Select Doctor:"), gbc);

        doctorComboBox = new JComboBox<>();
        for (Doctor doc : doctors) {
            doctorComboBox.addItem(doc.getDisplayName());
        }
        gbc.gridx = 1;
        panel.add(doctorComboBox, gbc);

        // Patient name (horizontal)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Patient Name:"), gbc);

        patientField = new JTextField();
        patientField.setBackground(new Color(255, 204, 204)); // Light maroon background for text fields
        patientField.setForeground(Color.BLACK); // Text color
        gbc.gridx = 1;
        panel.add(patientField, gbc);

        // Medication (horizontal)
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Medication / Illness:"), gbc);

        medicationField = new JTextField();
        medicationField.setBackground(new Color(255, 204, 204)); // Light maroon background for text fields
        medicationField.setForeground(Color.BLACK); // Text color
        gbc.gridx = 1;
        panel.add(medicationField, gbc);

        // Submit button
        JButton submitButton = new JButton("Submit Prescription");
        submitButton.setBackground(new Color(128, 0, 0)); // Maroon background for the button
        submitButton.setForeground(Color.WHITE); // White text on the button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);

        // Output area (horizontal)
        outputArea = new JTextArea(8, 40);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(255, 255, 204)); // Light yellow background for output area
        outputArea.setForeground(Color.BLACK); // Text color
        JScrollPane scrollPane = new JScrollPane(outputArea);
        gbc.gridy = 4;
        panel.add(scrollPane, gbc);

        submitButton.addActionListener(e -> submitPrescription());

        add(panel);
    }

    private void submitPrescription() {
        int selectedIndex = doctorComboBox.getSelectedIndex();
        Doctor selectedDoctor = doctors.get(selectedIndex);

        // Step 1: Admin check doctor availability
        if (!selectedDoctor.isAvailable()) {
            JOptionPane.showMessageDialog(this,
                    selectedDoctor.getDisplayName() + " is currently NOT available.",
                    "Doctor Unavailable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 2: Patient details
        String patientName = patientField.getText().trim();
        String medication = medicationField.getText().trim().toLowerCase();

        if (patientName.isEmpty() || medication.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in both patient name and medication/illness.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 3: Admin decides if the doctor is appropriate for the medication
        if (!isDoctorSuitable(selectedDoctor.getSpecialization(), medication)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "⚠️ " + selectedDoctor.getSpecialization() + " is not suitable for \"" + medication + "\".\n" +
                            "Do you still want to proceed with the prescription?",
                    "Doctor Not Suitable",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Step 4: Admin approves prescription
        Prescription prescription = new Prescription(
                patientName,
                medication,
                selectedDoctor.getDisplayName()
        );
        prescriptions.add(prescription);

        // Clear input fields
        patientField.setText("");
        medicationField.setText("");

        // Output success (display results after submission)
        outputArea.append("✅ " + prescription.getDetails() + "\n");
    }

    // Simplified logic for doctor suitability based on medication/illness
    private boolean isDoctorSuitable(String specialization, String medication) {
        // Check suitability logic here based on illness and specialization
        if (medication.contains("headache") && !specialization.equalsIgnoreCase("General Practitioner")) {
            return false;
        }
        if (medication.contains("skin") && !specialization.equalsIgnoreCase("Dermatologist")) {
            return false;
        }
        if ((medication.contains("heart") || medication.contains("chest")) && !specialization.equalsIgnoreCase("Cardiologist")) {
            return false;
        }
        if ((medication.contains("cough") || medication.contains("flu") || medication.contains("cold")) && 
            !specialization.equalsIgnoreCase("General Practitioner")) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PharmacyPrescriptionManager().setVisible(true);
        });
    }
}
