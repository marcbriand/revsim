package revsim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import revsim.driver.SimState;
import revsim.rendering.InfoListener;
import revsim.rendering.Info;

/**
 * The application's main frame.
 */
public class RevSimView extends FrameView implements InfoListener {

    private CommandListener commandListener;
    private volatile SimState latestState = SimState.Initial;
    private volatile long latestFramecount = 0;

    private class UIControlBean {
        public Long framecount;
        public Boolean rewindEnabled;
        public Boolean stepBackEnabled;
        public Boolean stepFwdEnabled;
        public Boolean goEnabled;
        public Boolean pauseEnabled;
        public Boolean resumeEnabled;
        public Boolean markEnabled;
        public Boolean skipEnabled;
        public BufferedImage image;
    }

    public RevSimView(SingleFrameApplication app) {
        super(app);
        initComponents();
        canvas1.setBackground(Color.BLACK);
    }

    public void setCommandListener (CommandListener l) {
        commandListener = l;
    }

    private boolean shouldEnableDec () {
        if (this.latestFramecount < 2) {
            return false;
        }
        if (this.latestState == SimState.Initial || this.latestState == SimState.Running) {
            return false;
        }
        return true;
    }

    public void onInfo (Info iaf) {
        UIControlBean bean = new UIControlBean();
        bean.image = (BufferedImage)iaf.image;
        bean.framecount = iaf.frame;
        if (iaf.frame != null) {
            this.latestFramecount = iaf.frame;
        }
        bean.stepBackEnabled = shouldEnableDec();
        setUIElements(bean);
    }

    public void onState (SimState state) {
        latestState = state;
        UIControlBean bean = new UIControlBean();
        if (state == SimState.Idle) {
            bean.goEnabled = true;
            bean.markEnabled = true;
            bean.pauseEnabled = false;
            bean.resumeEnabled = false;
            bean.rewindEnabled = true;
            bean.stepBackEnabled = true;
            bean.stepFwdEnabled = true;
            bean.skipEnabled = true;
        }
        if (state == SimState.Initial) {
            clearCanvas();
            bean.goEnabled = true;
            bean.markEnabled = false;
            bean.pauseEnabled = false;
            bean.resumeEnabled = false;
            bean.rewindEnabled = false;
            bean.stepBackEnabled = false;
            bean.stepFwdEnabled = true;
            bean.framecount = 0L;
            bean.skipEnabled = true;
        }
        if (state == SimState.Paused) {
            bean.goEnabled = true;
            bean.markEnabled = true;
            bean.pauseEnabled = false;
            bean.resumeEnabled = true;
            bean.rewindEnabled = true;
            bean.stepBackEnabled = true;
            bean.stepFwdEnabled = true;
            bean.skipEnabled = true;
        }
        if (state == SimState.Running) {
            bean.goEnabled = false;
            bean.markEnabled = true;
            bean.pauseEnabled = true;
            bean.resumeEnabled = false;
            bean.rewindEnabled = false;
            bean.stepBackEnabled = false;
            bean.stepFwdEnabled = false;
            bean.skipEnabled = false;
        }
        bean.stepBackEnabled = shouldEnableDec();

        setUIElements(bean);
    }

    // ui setters
    private void setFrameCount (Long frame) {
        String value = frame < 1 ? "" : frame.toString();
        this.framecount.setText(value);
    }

    private void setMemoryLeft (long freeMem) {
        String value = Long.toString(freeMem/1000);
        this.memoryLeft.setText(value + "K");
    }

    private void setSkipEnabled (boolean value) {
        this.skip.setEnabled(value);
        this.skipSpinner.setEnabled(value);
    }

    private void clearCanvas () {
        Graphics g = canvas1.getGraphics();
        g.clearRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
        g.dispose();
    }

    private void clear () {
        this.latestFramecount = 0;
        this.latestState = SimState.Initial;
        clearCanvas();
    }

    private void setImage (BufferedImage image) {
        clearCanvas();
        Graphics g = canvas1.getGraphics();
        g.drawImage(image, 0, 0, null);
    }

    private void setUIElements (final UIControlBean bean) {
         final RevSimView self = this;
         SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run (){
                if (bean.framecount != null) {
                    setFrameCount(bean.framecount);
                }
                if (bean.rewindEnabled != null) {
                    self.rewind.setEnabled(bean.rewindEnabled);
                }
                if (bean.stepBackEnabled != null) {
                    self.stepBack.setEnabled(bean.stepBackEnabled);
                }
                if (bean.stepFwdEnabled != null) {
                    self.stepFwd.setEnabled(bean.stepFwdEnabled);
                }
                if (bean.goEnabled != null) {
                    self.go.setEnabled(bean.goEnabled);
                }
                if (bean.pauseEnabled != null) {
                    self.pause.setEnabled(bean.pauseEnabled);
                }
                if (bean.markEnabled != null) {
                    self.saveImage.setEnabled(bean.markEnabled);
                }
                if (bean.image != null) {
                    self.setImage(bean.image);
                }
                if (bean.skipEnabled != null) {
                    self.setSkipEnabled(bean.skipEnabled);
                }
                self.setMemoryLeft(Runtime.getRuntime().freeMemory());
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = RevSimApp.getApplication().getMainFrame();
            aboutBox = new RevSimAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        RevSimApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        framecount = new javax.swing.JTextField();
        rewind = new javax.swing.JButton();
        stepBack = new javax.swing.JButton();
        stepFwd = new javax.swing.JButton();
        go = new javax.swing.JButton();
        pause = new javax.swing.JButton();
        saveImage = new javax.swing.JButton();
        canvas1 = new java.awt.Canvas();
        memoryLeft = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        skip = new javax.swing.JButton();
        skipSpinner = new javax.swing.JSpinner();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(1024, 800));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(revsim.RevSimApp.class).getContext().getResourceMap(RevSimView.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        framecount.setText(resourceMap.getString("framecount.text")); // NOI18N
        framecount.setName("framecount"); // NOI18N

        rewind.setText(resourceMap.getString("rewind.text")); // NOI18N
        rewind.setName("rewind"); // NOI18N
        rewind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rewindActionPerformed(evt);
            }
        });

        stepBack.setText(resourceMap.getString("stepBack.text")); // NOI18N
        stepBack.setName("stepBack"); // NOI18N
        stepBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepBackActionPerformed(evt);
            }
        });

        stepFwd.setText(resourceMap.getString("stepFwd.text")); // NOI18N
        stepFwd.setName("stepFwd"); // NOI18N
        stepFwd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepFwdActionPerformed(evt);
            }
        });

        go.setText(resourceMap.getString("go.text")); // NOI18N
        go.setName("go"); // NOI18N
        go.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goActionPerformed(evt);
            }
        });

        pause.setText(resourceMap.getString("pause.text")); // NOI18N
        pause.setName("pause"); // NOI18N
        pause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseActionPerformed(evt);
            }
        });

        saveImage.setText(resourceMap.getString("saveImage.text")); // NOI18N
        saveImage.setName("saveImage"); // NOI18N
        saveImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImageActionPerformed(evt);
            }
        });

        canvas1.setBackground(resourceMap.getColor("canvas1.background")); // NOI18N
        canvas1.setName("canvas1"); // NOI18N

        memoryLeft.setText(resourceMap.getString("memoryLeft.text")); // NOI18N
        memoryLeft.setName("memoryLeft"); // NOI18N
        memoryLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memoryLeftActionPerformed(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        skip.setText(resourceMap.getString("skip.text")); // NOI18N
        skip.setName("skip"); // NOI18N
        skip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skipActionPerformed(evt);
            }
        });

        skipSpinner.setName("skipSpinner"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(27, 27, 27))
                    .addComponent(framecount, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(rewind, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(stepBack, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(stepFwd, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(go, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(pause, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(saveImage, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(memoryLeft, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(skip, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(skipSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(canvas1, javax.swing.GroupLayout.PREFERRED_SIZE, 821, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(framecount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(memoryLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rewind)
                .addGap(18, 18, 18)
                .addComponent(stepBack)
                .addGap(18, 18, 18)
                .addComponent(stepFwd)
                .addGap(18, 18, 18)
                .addComponent(go)
                .addGap(18, 18, 18)
                .addComponent(pause)
                .addGap(18, 18, 18)
                .addComponent(saveImage)
                .addGap(18, 18, 18)
                .addComponent(skip)
                .addGap(18, 18, 18)
                .addComponent(skipSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(179, 179, 179))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(canvas1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(revsim.RevSimApp.class).getContext().getActionMap(RevSimView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void saveImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImageActionPerformed
        BufferedImage img = new BufferedImage(300, 400, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.RED);
        g2.drawRect(20, 20, 20, 20);
        Info info = new Info(img, null, null);
        onInfo(info);
        if (commandListener != null) {
            commandListener.saveImage();
        }
}//GEN-LAST:event_saveImageActionPerformed

    private void rewindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rewindActionPerformed
        if (commandListener != null) {
            commandListener.rewind();
        }
    }//GEN-LAST:event_rewindActionPerformed

    private void stepBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepBackActionPerformed
        if (commandListener != null) {
            commandListener.dec();
        }
    }//GEN-LAST:event_stepBackActionPerformed

    private void stepFwdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepFwdActionPerformed
        if (commandListener != null) {
            commandListener.inc();
        }
    }//GEN-LAST:event_stepFwdActionPerformed

    private void goActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goActionPerformed
        if (commandListener != null) {
            if (latestState == SimState.Initial || latestState == SimState.Idle){
                commandListener.go();
            }
            else if (latestState == SimState.Paused) {
                commandListener.resume();
            }
            else {
                System.out.println("go pushed while sim is running");
            }
        }
    }//GEN-LAST:event_goActionPerformed

    private void pauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseActionPerformed
        if (commandListener != null) {
            commandListener.pause();
        }
    }//GEN-LAST:event_pauseActionPerformed

    private void memoryLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memoryLeftActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_memoryLeftActionPerformed

    private void skipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skipActionPerformed
        if (commandListener != null) {
            long frame = (Integer)skipSpinner.getValue();
            commandListener.skipTo(frame);
        }
    }//GEN-LAST:event_skipActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Canvas canvas1;
    private javax.swing.JTextField framecount;
    private javax.swing.JButton go;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField memoryLeft;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton pause;
    private javax.swing.JButton rewind;
    private javax.swing.JButton saveImage;
    private javax.swing.JButton skip;
    private javax.swing.JSpinner skipSpinner;
    private javax.swing.JButton stepBack;
    private javax.swing.JButton stepFwd;
    // End of variables declaration//GEN-END:variables


    private JDialog aboutBox;
}
