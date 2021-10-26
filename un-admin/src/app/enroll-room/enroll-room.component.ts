import { stringify } from '@angular/compiler/src/util';
import { Component, OnInit } from '@angular/core';
import { Department } from '../model/department';
import { Room } from '../model/room';
import { Student } from '../model/student';
import { DepartmentService } from '../services/department.service';
import { RoomService } from '../services/room.service';
import { StudentService } from '../services/student.service'
import { StudentResponseDTO } from '../model/student.response';
import { DashboardService } from '../services/dashboard.service'
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';

@Component({
  selector: 'app-enroll-room',
  templateUrl: './enroll-room.component.html',
  styleUrls: ['./enroll-room.component.css']
})
export class EnrollRoomComponent implements OnInit {

  departments: Department[] = [];
  rooms: Room[] = [];
  students = new StudentResponseDTO();
  titleOfExam = '';
  invesiloterSize = 0;
  constructor(private departmentsService: DepartmentService,
    private roomService: RoomService,
    private studentService: StudentService,
    private dashboardService: DashboardService) {


  }

  ngOnInit(): void {
    this.invesilotersList = [];
    this.selectedRooms = [];
    this.invesiloterSize = 0;
    this.titleOfExam = '';
    this.students = new StudentResponseDTO();
    this.departmentsService.getAll().subscribe(res => {
      this.departments = res['data'];
      this.fileList = [];
    });
  }
  getSelectedBlock() {
    let selectedBlock = '';
    this.departments.forEach(e => {
      if (e.checked) {
        selectedBlock = selectedBlock + '  ,' + e.departmentName;
      }
    });
    selectedBlock = 'Selected Block' + selectedBlock.replace(',', '');
    return selectedBlock;
  }

  getDepartments() {
    let selectedIds = [];
    this.departments.forEach(e => {
      if (e.checked) {
        selectedIds.push(e.id);
      }
    });
    this.roomService.findByBlockIds(selectedIds).subscribe(res => {
      this.rooms = res['data']
    });
  }
  selectedRooms = [];
  getCount() {
    this.selectedRooms = [];
    let count = 0;
    this.rooms.forEach(e => {
      if (e.checked) {
        count = count + (e.noOfRow * e.rowCapacity);
        this.selectedRooms.push(e);
      }
    });
    return count;
  }

  fileList = []
  fileDataLoader(event) {
    this.fileList = event.target.files;
  }

  upload() {
    this.students = new StudentResponseDTO();
    this.studentService.upload(this.fileList).subscribe((res: any) => {
      this.students = res.body;
    });
  }
  getSelectedRoomDetails(room: Room) {
    return room.name + ' (' + (room.rowCapacity * room.noOfRow) + ')';
  }
  assignSeats() {
    let studentMapValue = [];
    for (let li of this.students.studentsList) {
      let newItems = [];
      for (let data of li) {
        newItems.push(data);
      }
      studentMapValue.push(newItems);
    }

    if (studentMapValue.length == 1) {
      let studentIndex = 0;
      for (let room of this.selectedRooms) {
        room.rollNumberList = [];
        let rowCapacity = room.rowCapacity;
        let noOfRow = room.noOfRow;

        for (let columnIndex = 0; columnIndex < rowCapacity; columnIndex++) {
          let columnValue = [];
          for (let rowNumber = 0; rowNumber < noOfRow; rowNumber++) {
            if (studentMapValue[0].length >= studentIndex && studentMapValue[0][studentIndex]) {
              columnValue.push(studentMapValue[0][studentIndex].roolNumber);
              studentIndex++;
            } else {
              continue;
            }
          }
          room.rollNumberList.push(columnValue);
        }
      }
    } else if (studentMapValue.length == 2) {
      let studentIndex = 0;
      let secondeStudentIndex = 0;
      for (let room of this.selectedRooms) {
        room.rollNumberList = [];
        let rowCapacity = room.rowCapacity;
        let noOfRow = room.noOfRow;

        for (let columnIndex = 0; columnIndex < rowCapacity; columnIndex++) {
          let columnValue = [];
          for (let rowNumber = 0; rowNumber < noOfRow; rowNumber++) {
            if (columnIndex % 2 !== 0) {
              if (studentMapValue[0].length >= studentIndex && studentMapValue[0][studentIndex]) {
                columnValue.push(studentMapValue[0][studentIndex].roolNumber);
                studentIndex++;
              } else {
                continue;
              }
            } else {
              if (studentMapValue[1].length >= secondeStudentIndex && studentMapValue[1][secondeStudentIndex]) {
                columnValue.push(studentMapValue[1][secondeStudentIndex].roolNumber);
                secondeStudentIndex++;
              } else {
                continue;
              }
            }
          }
          room.rollNumberList.push(columnValue);
        }
      }
    } else if (studentMapValue.length == 3) {
      let studentIndex = 0;
      let secondStudentIndex = 0;
      let firstSheetValueLength = studentMapValue[0].length;
      let secondSheetValueLength = studentMapValue[1].length;
      let thirdSheetValueLength = studentMapValue[2].length;
      let firstColumnCellValue = [];
      let secondColumnCellValue = [];
      if (firstSheetValueLength >= secondSheetValueLength + thirdSheetValueLength) {
        firstColumnCellValue = studentMapValue[0];
        secondColumnCellValue = studentMapValue[1];
        for (let ele of studentMapValue[2]) {
          secondColumnCellValue.push(ele);
        }

      } else if (secondSheetValueLength >= firstSheetValueLength + thirdSheetValueLength) {
        firstColumnCellValue = studentMapValue[1];
        secondColumnCellValue = studentMapValue[0];
        for (let ele of studentMapValue[2]) {
          secondColumnCellValue.push(ele);
        }

      } else if (thirdSheetValueLength >= firstSheetValueLength + secondSheetValueLength) {
        firstColumnCellValue = studentMapValue[2];
        secondColumnCellValue = studentMapValue[0];
        for (let ele of studentMapValue[1]) {
          secondColumnCellValue.push(ele);
        }
      } else {
        firstColumnCellValue = studentMapValue[0];

        for (let ele of studentMapValue[1]) {
          firstColumnCellValue.push(ele);
        }

        for (let ele of studentMapValue[2]) {
          secondColumnCellValue.push(ele);
        }
      }

      for (let room of this.selectedRooms) {
        room.rollNumberList = [];
        let rowCapacity = room.rowCapacity;
        let noOfRow = room.noOfRow;

        for (let columnIndex = 0; columnIndex < rowCapacity; columnIndex++) {
          let columnValue = [];
          for (let rowNumber = 0; rowNumber < noOfRow; rowNumber++) {
            if (columnIndex % 2 == 0) {
              if (firstColumnCellValue.length >= studentIndex && firstColumnCellValue[studentIndex]) {
                columnValue.push(firstColumnCellValue[studentIndex].roolNumber);
                studentIndex++;
              } else {
                continue;
              }
            } else {
              if (secondColumnCellValue.length >= secondStudentIndex && secondColumnCellValue[secondStudentIndex]) {
                columnValue.push(secondColumnCellValue[secondStudentIndex].roolNumber);
                secondStudentIndex++;
              } else {
                continue;
              }
            }
          }
          room.rollNumberList.push(columnValue);
        }
      }

    }
  }
  getAssignButtonStatus() {
    /* let seatCount = this.getCount();
    let countOfStudent = 0;
    if (this.students && this.students.keys && this.students.keys.length != 0)
      this.students.forEach((value: Student[], key: string) => {
        countOfStudent = countOfStudent + value.length;
      });
    if (seatCount >= countOfStudent) {
      return false;
    } else {
      return true;
    } */
  }
  counter(i: number) {
    return new Array(i);
  }
  exportStudentRoomAlwrtment() {
    let body = {
      'title': this.titleOfExam,
      'selectedRooms': this.selectedRooms,
      'selectedRoomsForInvesiloter': this.selectedRoomsForInvesiloter
    }
    this.dashboardService.getStudentArrangment(body);
  }

  selectedRoomsForInvesiloter = [];
  connectedTo = [];

  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex);
    }
  }

  invesilotersList: any;;
  uploadInvesiloter() {
    this.invesilotersList = null;
    this.studentService.uploadForInvesiloter('invesiloters', this.fileList).subscribe((res: any) => {
      this.invesilotersList = res.body;
      this.setInvesiloterData();
    });
  }

  setInvesiloterData() {
    this.selectedRoomsForInvesiloter = [];
    let currentDetials = {
      id: 'Invesiloters',
      invesiloters: this.invesilotersList
    }
    this.selectedRoomsForInvesiloter.push(currentDetials);
    for (let room of this.selectedRooms) {
      let currentDetials = {
        id: room.name,
        invesiloters: [
        ]
      }
      this.selectedRoomsForInvesiloter.push(currentDetials);
    }
    for (let inv of this.selectedRoomsForInvesiloter) {
      this.connectedTo.push(inv.id);
    };
  }
  exportInvesiloterList() {
    console.log(this.selectedRoomsForInvesiloter);
  }

  autoAssignInvesiloter() {

    this.invesilotersList = null;
    this.studentService.uploadForInvesiloter('invesiloters', this.fileList).subscribe((res: any) => {
      this.invesilotersList = res.body;
      this.setInvesiloterData();
      this.studentService.manageRoomAndInvesiloter(this.selectedRoomsForInvesiloter, this.selectedRooms, this.invesiloterSize).subscribe(res => {
        this.selectedRoomsForInvesiloter = res['alertsRooms'];
      });
    });
  }
}
