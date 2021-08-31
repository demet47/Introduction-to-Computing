import java.io.*;
import java.util.*;

public class DY2019400105 {
	public static void main(String[] args) throws FileNotFoundException {

		switch (Integer.parseInt(args[0])) {
		case 0:
			PrintArrayInfoToPPMFile(readPPMFileToArray(new File(args[1])), new File("output.ppm"));
			break;
		case 1:
			makeBlackNWhiteAndPrint(new File(args[1]), new File("black-and-white.ppm"));
			break;
		case 2:
			PrintArrayInfoToPPMFile(filtered(new File(args[2]), readPPMFileToArray(new File(args[1]))),
					new File("convolutionNotB&W.ppm"));
			makeBlackNWhiteAndPrint(new File("convolutionNotB&W.ppm"), new File("convolution.ppm"));
			break;
		case 3:
			int[][][] input = readPPMFileToArray(new File(args[1]));
			PrintArrayInfoToPPMFile(quantize(new boolean[input.length][input[0].length][3], input,
					Integer.parseInt(args[2]), 0, 0, 0, true), new File("quantized.ppm"));
			break;
		}

	}

	public static int[][][] readPPMFileToArray(File PPM) throws FileNotFoundException {

		Scanner fl = new Scanner(PPM);
		fl.nextLine();
		int cl = fl.nextInt();
		int rw = fl.nextInt();
		fl.nextLine();
		fl.nextLine();

		int[][][] array = new int[rw][cl][3];

		for (int row = 0; row < rw; row++) {
			for (int col = 0; col < cl; col++) {
				array[row][col][0] = fl.nextInt();
				array[row][col][1] = fl.nextInt();
				array[row][col][2] = fl.nextInt();
			}
		}

		return array;
	}

	public static void PrintArrayInfoToPPMFile(int[][][] PPM, File out) throws FileNotFoundException {

		PrintStream output = new PrintStream(out);

		output.println("P3");
		output.println(PPM[0].length + " " + (PPM.length));
		output.println("255");

		for (int row = 0; row < PPM.length; row++) {
			for (int col = 0; col < PPM[0].length; col++) {
				for (int color = 0; color < 3; color++) {
					output.print(PPM[row][col][color] + " ");
				}
				output.print("\t");
			}
			output.println();
		}

	}

	public static void makeBlackNWhiteAndPrint(File in, File out) throws FileNotFoundException {
		Scanner fl = new Scanner(in);
		PrintStream output = new PrintStream(out);

		output.println(fl.nextLine());
		int cl = fl.nextInt();
		int rw = fl.nextInt();
		output.println(cl + " " + rw);
		fl.nextLine();
		output.println(fl.nextLine());

		for (int row = 0; row < rw; row++) {
			for (int col = 0; col < cl; col++) {
				int average = (fl.nextInt() + fl.nextInt() + fl.nextInt()) / 3;
				output.print(average + " " + average + " " + average + " \t");
			}
			output.println();
		}
	}

	public static int[][][] filtered(File input, int[][][] PPM) throws FileNotFoundException {
		Scanner filt = new Scanner(input);

		String sz = filt.next();
		int size = Integer.parseInt(sz.substring(sz.indexOf("x") + 1));
		filt.nextLine();

		int[][] fltr = new int[size][size];

		for (int row = 0; row < size; row++)
			for (int col = 0; col < size; col++)
				fltr[row][col] = filt.nextInt();

		int[][][] convoluted = new int[PPM.length - size + 1][PPM[0].length - size + 1][3];

		for (int row = size / 2; row < PPM.length - size / 2; row++) {
			for (int col = size / 2; col < PPM[0].length - size / 2; col++) {
				for (int color = 0; color < 3; color++) {
					int sum = 0;
					for (int rw = -size / 2; rw <= size / 2; rw++)
						for (int cl = -size / 2; cl <= size / 2; cl++) {
							sum += PPM[row + rw][col + cl][color] * fltr[size / 2 + rw][size / 2 + cl];
						}
					if (sum < 0)
						sum = 0;
					if (sum > 255)
						sum = 255;

					convoluted[row - size / 2][col - size / 2][color] = sum;
				}
			}
		}

		return convoluted;

	}

	public static int[][][] quantize(boolean[][][] haveBeenHere, int[][][] input, int range, int row, int column,
			int color, boolean enter) {

		if (row == input.length - 1 && column == input[0].length - 1 && color == 2)
			return input;
		else {
			int value = input[row][column][color];

			if (!haveBeenHere[row][column][color]) {

				haveBeenHere[row][column][color] = true;

				if (column < input[0].length - 1 && !haveBeenHere[row][column + 1][color]
						&& input[row][column + 1][color] <= value + range
						&& input[row][column + 1][color] >= value - range) {
					input[row][column + 1][color] = value;
					quantize(haveBeenHere, input, range, row, column + 1, color, false);
				}

				if (column > 0 && !haveBeenHere[row][column - 1][color]
						&& input[row][column - 1][color] <= value + range
						&& input[row][column - 1][color] >= value - range) {
					input[row][column - 1][color] = value;
					quantize(haveBeenHere, input, range, row, column - 1, color, false);
				}

				if (row < input.length - 1 && !haveBeenHere[row + 1][column][color]
						&& input[row + 1][column][color] <= value + range
						&& input[row + 1][column][color] >= value - range) {
					input[row + 1][column][color] = value;
					quantize(haveBeenHere, input, range, row + 1, column, color, false);
				}

				if (row > 0 && !haveBeenHere[row - 1][column][color] && input[row - 1][column][color] <= value + range
						&& input[row - 1][column][color] >= value - range) {
					input[row - 1][column][color] = value;
					quantize(haveBeenHere, input, range, row - 1, column, color, false);
				}

				if (color < 2 && !haveBeenHere[row][column][color + 1] && input[row][column][color + 1] <= value + range
						&& input[row][column][color + 1] >= value - range) {
					input[row][column][color + 1] = value;
					quantize(haveBeenHere, input, range, row, column, color + 1, false);
				}

				if (color > 0 && !haveBeenHere[row][column][color - 1] && input[row][column][color - 1] <= value + range
						&& input[row][column][color - 1] >= value - range) {
					input[row][column][color - 1] = value;

					quantize(haveBeenHere, input, range, row, column, color - 1, false);
				}

			}

			if (enter) {
				if (column + 1 < input[0].length)
					return quantize(haveBeenHere, input, range, row, column + 1, color, true);
				else {
					if (row + 1 < input.length)
						return quantize(haveBeenHere, input, range, row + 1, 0, color, true);
					else
						return quantize(haveBeenHere, input, range, 0, 0, color + 1, true);
				}

			} else {
				return input;
			}
		}

	}

}