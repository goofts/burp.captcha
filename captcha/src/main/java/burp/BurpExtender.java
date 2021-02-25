package burp;

import entity.CaptchaEntity;
import ui.GUI;
import ui.Menu;
import utils.Util;
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender,ITab,IIntruderPayloadGeneratorFactory, IIntruderPayloadGenerator{
    public static IBurpExtenderCallbacks callbacks;
    public static IExtensionHelpers helpers;
    private String extensionName = "captcha";
    private String version ="1.1.1";
    public static boolean isShowIntruderResult = true; // 识别结果是否显示Intruder模块结果
    public static PrintWriter stdout;
    public static PrintWriter stderr;
    public static GUI gui;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks calllbacks) {
        this.callbacks = calllbacks;
        this.helpers = calllbacks.getHelpers();
        this.stdout = new PrintWriter(calllbacks.getStdout(),true);
        this.stderr = new PrintWriter(calllbacks.getStderr(),true);
        gui = new GUI();
        callbacks.setExtensionName(String.format("%s",extensionName));
        calllbacks.registerContextMenuFactory(new Menu());
        calllbacks.registerIntruderPayloadGeneratorFactory(this);

        stdout = new PrintWriter(callbacks.getStdout(),true);
        stderr = new PrintWriter(callbacks.getStderr(),true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BurpExtender.this.callbacks.addSuiteTab(BurpExtender.this);
            }
        });
        stdout.println(Util.getBanner(extensionName,version));

    }



    @Override
    public String getTabCaption() {
        return extensionName;
    }

    @Override
    public Component getUiComponent() {
        return gui.getComponet();
    }

    @Override
    public boolean hasMorePayloads() {
        return true;
    }

    @Override
    public byte[] getNextPayload(byte[] bytes) {
        GeneratePayloadSwingWorker gpsw = new GeneratePayloadSwingWorker();
        gpsw.execute();
        try {
            Object result = gpsw.get();
            return (byte[])result;
        }catch (Exception e){
            e.printStackTrace();
            return String.format("Erro: %s",e.getMessage()).getBytes();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public String getGeneratorName() {
        return this.extensionName;
    }

    @Override
    public IIntruderPayloadGenerator createNewInstance(IIntruderAttack iIntruderAttack) {
        return this;
    }
}