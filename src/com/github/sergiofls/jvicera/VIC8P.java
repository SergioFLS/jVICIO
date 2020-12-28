package com.github.sergiofls.jvicera;

/**
 * VIC-8P implementation in Java
 * i never wrote jdocs lol
 *
 * @since now
 */

public class VIC8P {
	private static int REGISTER_A = 0;
	private static int REGISTER_B = 1;
	private static int REGISTER_C = 2;
	private static int REGISTER_D = 3;
	private static int REGISTER_E = 4;
	private static int REGISTER_H = 5;
	private static int REGISTER_L = 6;
	
	public byte[] memory = new byte[0x10000];
	
	private boolean zeroFlag = false;
	private boolean carryFlag = false;
	
	private short programCounter = 0;
	private short stackPointer = 0;
	private byte[] registers = new byte[7];
	
	public VIC8P() {
		memory[0] = (byte)0x40;
		memory[1] = (byte)0xC8;
		
		registers[REGISTER_H] = (byte)0xCA;
		registers[REGISTER_L] = (byte)0xFE;
	}
	
	private int unsignedByte(byte number) {
		return number&0xFF;
	}
	
	private int mergeBytes(byte high, byte low) {
		return (unsignedByte(high) << 8) | unsignedByte(low);
	}
	
	// MOV R, R
	private void movRtoR(int register1, int register2) {
		if (register1 > 7 || register2 > 7) System.err.println("movRtoR params overflow");
		
		registers[register1] = registers[register2];
	}
	
	// MOV (NNNN), R
	private void movAddrToR(int address, int register) {
		if (register > 7) System.err.println("movAddrToR params overflow");
		
		memory[address] = registers[register];
	}
	
	private void dumpRegisters() {
		System.out.println("// dumpRegisters \\\\");
		System.out.println(zeroFlag ? "Zero" : "NonZero");
		System.out.println(carryFlag ? "Carry" : "NC");
		System.out.println(
			"A=" + unsignedByte(registers[REGISTER_A]) + "\n" +
			"B=" + unsignedByte(registers[REGISTER_B]) + "\n" +
			"C=" + unsignedByte(registers[REGISTER_C]) + "\n" +
			"D=" + unsignedByte(registers[REGISTER_D]) + "\n" +
			"E=" + unsignedByte(registers[REGISTER_E]) + "\n" +
			"H=" + unsignedByte(registers[REGISTER_H]) + "\n" +
			"L=" + unsignedByte(registers[REGISTER_L])
		);
		System.out.println(
			"BC=" + mergeBytes(registers[REGISTER_B], registers[REGISTER_C]) + "\n" +
			"DE=" + mergeBytes(registers[REGISTER_D], registers[REGISTER_E]) + "\n" +
			"HL=" + mergeBytes(registers[REGISTER_H], registers[REGISTER_L])
		);
	}
	
	public void step() {
		byte opcode = memory[programCounter++];
		System.out.println("PC="+programCounter);
		switch (unsignedByte(opcode)) {
			case 0x0A:	// MOV A, A
			case 0x12:	// MOV B, B
			case 0x1A:	// MOV C, C
			case 0x22:	// MOV D, D
			case 0x2A:	// MOV E, E
			case 0x32:	// MOV H, H
			case 0x3A:	// MOV L, L
				break;
			case 0x0B:	// MOV A, B
			case 0x0C:	// MOV A, C
			case 0x0D:	// MOV A, D
			case 0x0E:	// MOV A, E
			case 0x0F:	// MOV A, H
			case 0x10:	// MOV A, L
				movRtoR(REGISTER_A, unsignedByte(opcode)-0x0A);
				break;
			case 0x11:	// MOV B, A
			case 0x13:	// MOV B, C
			case 0x14:	// MOV B, D
			case 0x15:	// MOV B, E
			case 0x16:	// MOV B, H
			case 0x17:	// MOV B, L
				movRtoR(REGISTER_B, unsignedByte(opcode)-0x11);
				break;
			case 0x18:	// MOV C, A
			case 0x19:	// MOV C, B
			case 0x1B:	// MOV C, D
			case 0x1C:	// MOV C, E
			case 0x1D:	// MOV C, H
			case 0x1E:	// MOV C, L
				movRtoR(REGISTER_C, unsignedByte(opcode)-0x18);
				break;
			case 0x1F:	// MOV D, A
			case 0x20:	// MOV D, B
			case 0x21:	// MOV D, C
			case 0x23:	// MOV D, E
			case 0x24:	// MOV D, H
			case 0x25:	// MOV D, L
				movRtoR(REGISTER_D, unsignedByte(opcode)-0x1F);
				break;
			case 0x26:	// MOV E, A
			case 0x27:	// MOV E, B
			case 0x28:	// MOV E, C
			case 0x29:	// MOV E, D
			case 0x2B:	// MOV E, H
			case 0x2C:	// MOV E, L
				movRtoR(REGISTER_E, unsignedByte(opcode)-0x26);
				break;
			case 0x2D:	// MOV H, A
			case 0x2E:	// MOV H, B
			case 0x2F:	// MOV H, C
			case 0x30:	// MOV H, D
			case 0x31:	// MOV H, E
			case 0x33:	// MOV H, L
				movRtoR(REGISTER_H, unsignedByte(opcode)-0x2D);
				break;
			case 0x34:	// MOV L, A
			case 0x35:	// MOV L, B
			case 0x36:	// MOV L, C
			case 0x37:	// MOV L, D
			case 0x38:	// MOV L, E
			case 0x39:	// MOV L, H
				movRtoR(REGISTER_L, unsignedByte(opcode)-0x34);
				break;
			case 0x3B:	// MOV (HL), A
			case 0x3C:	// MOV (HL), B
			case 0x3D:	// MOV (HL), C
			case 0x3E:	// MOV (HL), D
			case 0x3F:	// MOV (HL), E
			case 0x40:	// MOV (HL), H
			case 0x41:	// MOV (HL), L
				movAddrToR(mergeBytes(registers[REGISTER_H], registers[REGISTER_L]), unsignedByte(opcode)-0x3B);
				break;
			case 0xC8:	// DUMPR
				dumpRegisters();
				break;
			default:
				System.out.println(unsignedByte(opcode));
				break;
		}
	}
}
