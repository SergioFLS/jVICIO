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
	
	private char programCounter = 0;
	private char stackPointer = 0;
	private byte[] registers = new byte[7];
	
	public boolean halted = false;
	
	public VIC8P() {
		System.out.println("New CPU created!");
	}
	
	private int unsignedByte(byte number) {
		return number&0xFF;
	}
	
	private byte getHigh(int u2) {
		return (byte)(u2 >> 8);
	}
	
	private byte getLow(int u2) {
		return (byte)(u2 & 0xFF);
	}
	
	private int mergeBytes(byte high, byte low) {
		return (unsignedByte(high) << 8) | unsignedByte(low);
	}
	
	// MOV R, R
	private void movRtoR(int register1, int register2) {
		if (register1 > 7 || register2 > 7) System.err.println("movRtoR params overflow");
		
		registers[register1] = registers[register2];
	}
	
	// MOV R, NN
	private void movRtoVal(int register, int value) {
		registers[register] = (byte)value;
	}
	
	// MOV R, (NNNN)
	private void movRtoAddr(int register, int address) {
		registers[register] = memory[address];
	}
	
	// MOV RR, NNNN
	private void movRRtoValVal(int register, int value) {
		int highVal = getHigh(value);
		int lowVal = getLow(value);
		
		switch (register) {
			case 0:	// HL
				movRtoVal(REGISTER_H, highVal);
				movRtoVal(REGISTER_L, lowVal);
				break;
			case 1:	// BC
				movRtoVal(REGISTER_B, highVal);
				movRtoVal(REGISTER_C, lowVal);
				break;
			case 2:	// DE
				movRtoVal(REGISTER_D, highVal);
				movRtoVal(REGISTER_E, lowVal);
				break;
		}
	}
	
	// CP
	private void compare(int value) {
		zeroFlag = registers[REGISTER_A] == value;
		carryFlag = registers[REGISTER_A] < value;
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
		System.out.println("\\\\ dumpRegisters //");
	}
	
	public void step() throws Exception {
		if (!halted) {
			byte opcode = memory[programCounter++];
			switch (unsignedByte(opcode)) {
				case 0x00:	// HALT
					halted = true;
					break;
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
				case 0x42:	// MOV A, NN
				case 0x43:	// MOV B, NN
				case 0x44:	// MOV C, NN
				case 0x45:	// MOV D, NN
				case 0x46:	// MOV E, NN
				case 0x47:	// MOV H, NN
				case 0x48:	// MOV L, NN
					movRtoVal(unsignedByte(opcode)-0x42, memory[programCounter++]);
					break;
				case 0x55:	// MOV A, (DE)
					movRtoAddr(REGISTER_A, mergeBytes(registers[REGISTER_D], registers[REGISTER_E]));
					break;
				case 0x59:	// MOV (NNNN), A
					byte highMovNNNN = (byte)memory[programCounter++];
					byte lowMovNNNN = (byte)memory[programCounter++];
					
					movAddrToR(mergeBytes(highMovNNNN, lowMovNNNN), REGISTER_A);
					break;
				case 0x5A:	// MOV HL, NNNN
				case 0x5B:	// MOV BC, NNNN
				case 0x5C:	// MOV DE, NNNN
					byte high = (byte)memory[programCounter++];
					byte low = (byte)memory[programCounter++];
					
					movRRtoValVal(opcode-0x5A, mergeBytes(high, low));
					break;
				case 0xB4:	// CP A
				case 0xB5:	// CP B
				case 0xB6:	// CP C
				case 0xB7:	// CP D
				case 0xB8:	// CP E
				case 0xB9:	// CP H
				case 0xBA:	// CP L
					System.out.println("compare");
					compare(unsignedByte(registers[unsignedByte(opcode)-0xB4]));
					break;
				case 0xBD:	// JP NNNN
					byte highJP = (byte)memory[programCounter++];
					byte lowJP = (byte)memory[programCounter++];
					
					programCounter = (char)mergeBytes(highJP, lowJP);
					break;
				case 0xC8:	// DUMPR
					dumpRegisters();
					break;
				default:
					dumpRegisters();
					throw new Exception("Unknown opcode "+Integer.toString(unsignedByte(opcode))+" at PC="+Integer.toString(programCounter&0xFFFF));
			}
		} else {
			throw new Exception("The CPU is halted!");
		}
	}
}
