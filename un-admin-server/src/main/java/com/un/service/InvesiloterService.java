package com.un.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.un.dto.AlertRoom;
import com.un.dto.InvesilotersDTO;
import com.un.dto.RoomInveiloterDTO;

@Service
public class InvesiloterService {

	public List<InvesilotersDTO> processStudentFile(XSSFWorkbook workbook) {
		List<InvesilotersDTO> invesiloters = new LinkedList<>();
		for (Sheet sheet : workbook) {
			boolean headerstatus = true;
			for (Row row : sheet) {
				if (headerstatus) {
					headerstatus = false;
					continue;
				}
				Cell nameCell = row.getCell(0);
				String name = getValue(nameCell);
				Cell deptCell = row.getCell(1);
				String department = getValue(deptCell);
				InvesilotersDTO invesiloter = new InvesilotersDTO();
				invesiloter.setName(name);
				invesiloter.setDepartment(department);
				invesiloters.add(invesiloter);
			}
			break;
		}
		return invesiloters;

	}

	private String getValue(Cell cell) {
		String data = "";
		try {
			return cell.getBooleanCellValue() + "";
		} catch (Exception e) {
		}
		try {
			return cell.getStringCellValue();
		} catch (Exception e) {
		}
		try {
			return Double.valueOf(cell.getNumericCellValue()).longValue() + "";
		} catch (Exception e) {
		}
		return data;
	}

	public RoomInveiloterDTO mappingRoomAndInvesiloter(RoomInveiloterDTO dto) {
		List<AlertRoom> alertsRooms = dto.getAlertsRooms();
		Map<String, List<AlertRoom>> mapOfRooms = alertsRooms.stream()
				.filter(p -> p.getId() != null && !p.getId().isEmpty())
				.collect(Collectors.groupingBy(AlertRoom::getId));

		List<AlertRoom> invesiloterData = mapOfRooms.get("Invesiloters");

		Map<String, List<InvesilotersDTO>> invesiloterDepartmentMap = mapOfRooms.get("Invesiloters").stream()
				.filter(p -> !p.getInvesiloters().isEmpty()).flatMap(p -> p.getInvesiloters().stream())
				.filter(p -> p.getDepartment() != null && !p.getDepartment().isEmpty())
				.collect(Collectors.groupingBy(InvesilotersDTO::getDepartment));
		List<String> invesiloterDepartmentMapKeySet = new ArrayList<>(invesiloterDepartmentMap.keySet());

		int totalRoomToAlert = mapOfRooms.keySet().size() - 1;

		List<InvesilotersDTO> allInvesticators = invesiloterData.get(0).getInvesiloters();
		allInvesticators.sort(Comparator.comparing(InvesilotersDTO::getDepartment).thenComparing(InvesilotersDTO::getName));
		if (totalRoomToAlert > allInvesticators.size()) {
			return null;
		}
		int invesiloterSize = dto.getInvesiloterSize();
		if (invesiloterSize == 0) {
			invesiloterSize = 1;
		}
		if (invesiloterSize == 1 || invesiloterDepartmentMapKeySet.size() == 1) {
			int i = 0;
			for (String key : mapOfRooms.keySet()) {
				setInvesiloterToRoom(mapOfRooms, allInvesticators, i, key);
			}
		} else if (invesiloterSize == 2) {
			int i = 0;
			for (String key : mapOfRooms.keySet()) {
				setInvesiloterToRoom(mapOfRooms, allInvesticators, i, key);
			}
			for (String key : mapOfRooms.keySet()) {
				if ("Invesiloters".equals(key)) {
					continue;
				} else {
					setInvesiloterToAlredyPresentList(mapOfRooms, allInvesticators, i, key);
				}
			}
		} else if (invesiloterSize == 3) {
			int i = 0;
			for (String key : mapOfRooms.keySet()) {
				setInvesiloterToRoom(mapOfRooms, allInvesticators, i, key);
			}
			for (String key : mapOfRooms.keySet()) {
				if ("Invesiloters".equals(key)) {
					continue;
				} else {
					setInvesiloterToAlredyPresentList(mapOfRooms, allInvesticators, i, key);
				}
			}
			for (String key : mapOfRooms.keySet()) {
				if ("Invesiloters".equals(key)) {
					continue;
				} else {
					setInvesiloterToAlredyPresentList(mapOfRooms, allInvesticators, i, key);
				}
			}
		} else if (invesiloterSize == 4) {
			int i = 0;
			for (String key : mapOfRooms.keySet()) {
				setInvesiloterToRoom(mapOfRooms, allInvesticators, i, key);
			}
			for (String key : mapOfRooms.keySet()) {
				if ("Invesiloters".equals(key)) {
					continue;
				} else {
					setInvesiloterToAlredyPresentList(mapOfRooms, allInvesticators, i, key);
				}
			}
			for (String key : mapOfRooms.keySet()) {
				if ("Invesiloters".equals(key)) {
					continue;
				} else {
					setInvesiloterToAlredyPresentList(mapOfRooms, allInvesticators, i, key);
				}
			}
			for (String key : mapOfRooms.keySet()) {
				if ("Invesiloters".equals(key)) {
					continue;
				} else {
					setInvesiloterToAlredyPresentList(mapOfRooms, allInvesticators, i, key);
				}
			}
		}
		return dto;
	}

	private void setInvesiloterToAlredyPresentList(Map<String, List<AlertRoom>> mapOfRooms,
			List<InvesilotersDTO> allInvesticators, int i, String key) {
		if("Invesiloters".equals(key) || allInvesticators.isEmpty()) {
			return;
		}
		AlertRoom room = mapOfRooms.get(key).get(0);
		List<InvesilotersDTO> invesiloters = room.getInvesiloters();
		invesiloters.add(allInvesticators.get(i));
		room.setInvesiloters(invesiloters);
		allInvesticators.remove(i);
	}

	private void setInvesiloterToRoom(Map<String, List<AlertRoom>> mapOfRooms, List<InvesilotersDTO> allInvesticators,
			int i, String key) {
		if ("Invesiloters".equals(key) || allInvesticators.isEmpty()) {
			return;
		} else {
			AlertRoom room = mapOfRooms.get(key).get(0);
			List<InvesilotersDTO> invesiloters = new ArrayList<>();
			invesiloters.add(allInvesticators.get(i));
			room.setInvesiloters(invesiloters);
			allInvesticators.remove(i);
		}
	}
}
