package com.github.sergiofls.jvicio;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;

import com.github.sergiofls.jvicera.VIC8P;

public class jVICIOMIDlet extends MIDlet {
	private short someByte = (short)65535;
	protected void startApp() {
		VIC8P cpu = new VIC8P();
		DataInputStream romHandler = new DataInputStream(super.getClass().getResourceAsStream("/hello.bin"));
		try {
			romHandler.readFully(cpu.memory);
		} catch (EOFException eofex) {} catch (IOException ioex) {};
		try {
			while (!cpu.halted) cpu.step();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println(cpu.memory[0xCAFE]&0xFF);
		Display display = Display.getDisplay(this);
		
		Form mainForm = new Form("jVICIO");
		mainForm.append("Check STDOUT!");
		
		display.setCurrent(mainForm);
	}
	
	protected void pauseApp() {};
	public void destroyApp(boolean unconditional) {};
}