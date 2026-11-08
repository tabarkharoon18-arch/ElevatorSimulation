
package elevatorsimulation;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
public class ElevatorSimulation extends JFrame {

    // بيانات المصعد
    private int currentFloor = 0;
    private int targetFloor = 0;

    private boolean doorsOpen = false;
    private int doorTicks = 0;

    private Queue<Integer> requests = new LinkedList<>();

    // عناصر الواجهة
    private JLabel floorLabel;
    private JLabel statusLabel;
    private JLabel directionLabel;

    private ElevatorPanel elevatorPanel;
    private Timer timer;

    public ElevatorSimulation() {

        setTitle("Elevator Simulation");
        setSize(750, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // الجزء العلوي
        JPanel topPanel = new JPanel(new GridLayout(1, 3));

        floorLabel = new JLabel("Current Floor: 0", JLabel.CENTER);
        statusLabel = new JLabel("Status: Waiting", JLabel.CENTER);
        directionLabel = new JLabel("Direction: Stop", JLabel.CENTER);

        topPanel.add(floorLabel);
        topPanel.add(statusLabel);
        topPanel.add(directionLabel);

        add(topPanel, BorderLayout.NORTH);

        // منطقة رسم المصعد
        elevatorPanel = new ElevatorPanel();
        add(elevatorPanel, BorderLayout.CENTER);

        // أزرار الطوابق
        JPanel controlPanel = new JPanel(new GridLayout(8, 1, 5, 5));

        JLabel title = new JLabel("Choose Floor", JLabel.CENTER);
        controlPanel.add(title);

        for (int i = 5; i >= 0; i--) {

            final int floor = i;

            JButton button = new JButton("Floor " + floor);

            button.addActionListener(e -> requestFloor(floor));

            controlPanel.add(button);
        }

        JButton resetButton = new JButton("RESET");

        resetButton.addActionListener(e -> resetElevator());

        controlPanel.add(resetButton);

        add(controlPanel, BorderLayout.EAST);

        // مؤقت حركة المصعد
        timer = new Timer(700, e -> moveElevator());

        timer.start();
    }

    // طلب طابق معين
    private void requestFloor(int floor) {

        if (floor < 0 || floor > 5) {
            return;
        }

        if (floor == currentFloor && requests.isEmpty()
                && !doorsOpen) {

            doorsOpen = true;
            doorTicks = 2;
            statusLabel.setText("Status: Doors Open");

            elevatorPanel.repaint();
            return;
        }

        if (floor != currentFloor && !requests.contains(floor)
                && floor != targetFloor) {

            requests.add(floor);

            statusLabel.setText("Status: Request Added");
        }
    }

    // حركة المصعد
    private void moveElevator() {

        // إذا كانت الأبواب مفتوحة
        if (doorsOpen) {

            doorTicks--;

            if (doorTicks <= 0) {

                doorsOpen = false;

                statusLabel.setText("Status: Moving");

            } else {

                statusLabel.setText("Status: Doors Open");
            }

            updateDisplay();
            return;
        }

        // اختيار الطابق التالي
        if (currentFloor == targetFloor) {

            if (requests.isEmpty()) {

                statusLabel.setText("Status: Waiting");
                directionLabel.setText("Direction: Stop");

                updateDisplay();
                return;
            }

            targetFloor = requests.poll();
        }

        // التحرك للأعلى
        if (currentFloor < targetFloor) {

            currentFloor++;

            directionLabel.setText("Direction: UP");

            statusLabel.setText("Status: Moving Up");
        }

        // التحرك للأسفل
        else if (currentFloor > targetFloor) {

            currentFloor--;

            directionLabel.setText("Direction: DOWN");

            statusLabel.setText("Status: Moving Down");
        }

        // الوصول إلى الطابق المطلوب
        if (currentFloor == targetFloor) {

            doorsOpen = true;

            doorTicks = 2;

            statusLabel.setText("Status: Doors Open");

            directionLabel.setText("Direction: Stop");
        }

        updateDisplay();
    }

    // تحديث معلومات الشاشة
    private void updateDisplay() {

        floorLabel.setText("Current Floor: " + currentFloor);

        elevatorPanel.repaint();
    }

    // إعادة ضبط المصعد
    private void resetElevator() {

        currentFloor = 0;
        targetFloor = 0;

        requests.clear();

        doorsOpen = false;
        doorTicks = 0;

        statusLabel.setText("Status: Waiting");
        directionLabel.setText("Direction: Stop");

        updateDisplay();
    }

    // رسم المصعد والطوابق
    class ElevatorPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            int panelHeight = getHeight();

            int floorGap = (panelHeight - 60) / 6;

            int shaftX = 120;
            int shaftWidth = 180;

            // رسم مبنى الطوابق
            g2.setColor(Color.DARK_GRAY);

            for (int i = 0; i < 6; i++) {

                int y = 30 + i * floorGap;

                g2.drawLine(40, y, 400, y);

                g2.drawString("Floor " + (5 - i), 15, y - 5);
            }

            // رسم بئر المصعد
            g2.drawRect(shaftX, 30, shaftWidth,
                    floorGap * 5);

            // حساب موقع المصعد
            int elevatorY = 30
                    + (5 - currentFloor) * floorGap;

            // لون المصعد
            if (doorsOpen) {
                g2.setColor(Color.GREEN);
            } else {
                g2.setColor(Color.BLUE);
            }

            // رسم عربة المصعد
            g2.fillRect(
                    shaftX + 20,
                    elevatorY + 5,
                    shaftWidth - 40,
                    floorGap - 10
            );

            // رقم الطابق داخل المصعد
            g2.setColor(Color.WHITE);

            g2.drawString(
                    "Elevator",
                    shaftX + 60,
                    elevatorY + floorGap / 2
            );

            g2.drawString(
                    "Floor: " + currentFloor,
                    shaftX + 55,
                    elevatorY + floorGap / 2 + 20
            );

            // حالة الأبواب
            if (doorsOpen) {

                g2.setColor(Color.BLACK);

                g2.drawString(
                        "DOORS OPEN",
                        shaftX + 50,
                        elevatorY - 5
                );
            }
        }
    }
   
    public static void main(String[] args) {
        
       SwingUtilities.invokeLater(() -> {

            ElevatorSimulation elevator =
                    new ElevatorSimulation();

            elevator.setVisible(true);
        }); 
    }
    
}
