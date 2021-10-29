package com.un.util;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import com.un.dto.RollNumberForSeatArrangement;

public class MemoPatternGenrater {
	public static List<String> getMemoString(List<RollNumberForSeatArrangement> seats, String prefix) {
		List<List<RollNumberForSeatArrangement>> presentStudentList = new ArrayList<>();
		List<RollNumberForSeatArrangement> pStudents = new ArrayList<>();
		List<Integer> absentStudent = new ArrayList<>();
		int index = 0;
		for (RollNumberForSeatArrangement seat : seats) {
			index++;
			if (seat.isPresent()) {
				pStudents.add(seat);
				if(index == seats.size()) {
					presentStudentList.add(pStudents);
				}
			} else {
				if(!pStudents.isEmpty()) {
					presentStudentList.add(pStudents);					
				}
				pStudents = new ArrayList<>();
				absentStudent.add(seat.getRollNumber());
			}
		}
		String output = "";
		long count = presentStudentList.stream().flatMap(d -> d.stream()).count();
		for (List<RollNumberForSeatArrangement> sts : presentStudentList) {
			IntSummaryStatistics collect = sts.stream().collect(Collectors.summarizingInt(RollNumberForSeatArrangement::getRollNumber));
			int min = collect.getMin();
			int max = collect.getMax();

			if (min == max) {
				output = output + getDataWithPrefix(min) + ", ";
			} else if (min == (max - 1)) {
				output = output + getDataWithPrefix(min) + ", " + getDataWithPrefix(max) + ", ";
			} else {
				output = output + getDataWithPrefix(min) + " - " + getDataWithPrefix(max) + " , ";
			}
		}

		List<String> outputStrings = new ArrayList<>();
		outputStrings.add((prefix +"" + (output.substring(0, output.length()-2))).toUpperCase().trim()+ " = "+count);
		String absentString = absentStudent.stream().filter(p -> p!= null)
				.map(rollnumber -> prefix+ getDataWithPrefix(rollnumber))
				.collect(Collectors.joining(", "));
		outputStrings.add(absentString+" = " + absentStudent.size());
		return outputStrings;
	}
	
	private static String getDataWithPrefix(int number) {
		if (number <= 9) {
			return "00" + number;
		} else if (number <= 99) {
			return "0" + number;
		} else {
			return "" + number;
		}
	}
}
