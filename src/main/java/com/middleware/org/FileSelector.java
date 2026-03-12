package com.middleware.org;

import javax.swing.*;

public class FileSelector {
    public static String selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public static String selectSavePath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择输出文件保存位置");
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}