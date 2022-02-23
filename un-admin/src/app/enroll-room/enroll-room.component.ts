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
import { MemoService } from '../services/memo.service';
import { ConfirmationService, ConfirmEventType, MessageService } from 'primeng/api';
import { NotificationService } from '../notification.service';

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
  nameOfExam = '';
  dateOfExam ='';
  sittingOfExam='';
  invesiloterSize = 0;
  rollNumberLength = 0;

  constructor(private departmentsService: DepartmentService,
    private roomService: RoomService,
    private studentService: StudentService,
    private dashboardService: DashboardService,
    private memoService: MemoService,
    private confirmationService: ConfirmationService,
    private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.invesilotersList = [];
    this.selectedRooms = [];
    this.invesiloterSize = 0;
    this.titleOfExam = '';
    this.nameOfExam = '';
    this.dateOfExam = '';
    this.sittingOfExam = '';
    this.rollNumberLength;
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
    let count = 0;
    this.rooms.forEach(e => {
      if (e.checked) {
        count = count + (e.noOfRow * e.rowCapacity);
        //this.selectedRooms.push(e);
      }
    });
    return count;
  }

  addOrRemoveRoom(event : Room) {
    if(!this.selectedRooms) {
      this.selectedRooms = [];
    }
    if(event.checked) {
      this.selectedRooms.push(event);
    } else {
      this.selectedRooms = this.selectedRooms.filter(r => r.id != event.id)
    }
    this.selectedRooms = JSON.parse(JSON.stringify(this.selectedRooms))
  }

  fileList = []
  fileDataLoader(event) {
    this.fileList = event.target.files;
  }

  upload() {
    this.students = new StudentResponseDTO();
    this.studentService.upload(this.fileList).subscribe((res: any) => {
      this.students = res.body;
      this.assignSeats();
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
      this.forMultiRow(studentMapValue);
      
    } else {

      let studentValue = [];
      for(let rIndex = 0 ; rIndex < studentMapValue.length; rIndex++) {
        for(let data of studentMapValue[rIndex]) {
          studentValue.push(data);
        }
      }
     
      let newStudentMapValue = [];
      newStudentMapValue[0] = [];
      newStudentMapValue[1] = [];
      let half = Math.round(studentValue.length / 2);
      for(let i = 0 ; i < half; i++){
        newStudentMapValue[0].push(studentValue[i]);
      }
      for(let i = half; i < studentValue.length; i++){
        newStudentMapValue[1].push(studentValue[i]);
      }
      this.forMultiRow(newStudentMapValue);
    }
  }

  forMultiRow(studentMapValue) {
    let studentIndex = 0;
      let secondeStudentIndex = 0;
      for (let room of this.selectedRooms) {
        room.rollNumberList = [];
        let rowCapacity = room.rowCapacity;
        let noOfRow = room.noOfRow;

        for (let columnIndex = 0; columnIndex < rowCapacity; columnIndex++) {
          let columnValue = [];
          for (let rowNumber = 0; rowNumber < noOfRow; rowNumber++) {
            if (columnIndex % 2 == 0) {
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
  }
  alloctaedSeatCount = 0;
  uploadedSTudentCounts = 0;
  getAssignButtonStatus() {
    if (!this.selectedRooms || !this.students || !this.students.studentsList) {
      return;
    }
    let alloctaedSeatCount = 0;
    for (let room of this.selectedRooms) {
      if (room && room.rollNumberList) {
        for (let rolls of room.rollNumberList) {
          if (rolls != null) {
            alloctaedSeatCount = alloctaedSeatCount + rolls.length;
          }
        }
      }
    }
    let uploadedSTudentCounts = 0;
    for (let li of this.students.studentsList) {
      if (li != null) {
        uploadedSTudentCounts = uploadedSTudentCounts + li.length
      }
    }

    this.alloctaedSeatCount = alloctaedSeatCount;
    this.uploadedSTudentCounts = uploadedSTudentCounts;
    if (alloctaedSeatCount < uploadedSTudentCounts && alloctaedSeatCount != 0 && uploadedSTudentCounts != 0) {
      return true;
    } else {
      return false;
    }
  }
  counter(i: number) {
    return new Array(i);
  }
  exportStudentRoomAlwrtment() {
    let body = {
      'dateOfExam':this.dateOfExam,
      'sittingOfExam': this.sittingOfExam,
      'title': this.titleOfExam,
      'examName': this.nameOfExam,
      'selectedRooms': this.selectedRooms,
      'selectedRoomsForInvesiloter': this.selectedRoomsForInvesiloter,
      'rollNumberLength':this.rollNumberLength
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
      if (room.rollNumberList != null && room.rollNumberList.length != 0) {
        let studentCount = 0;
        for (let rools of room.rollNumberList) {
          studentCount = studentCount + rools.length;
        }
        if (studentCount != 0) {
          let currentDetials = {
            id: room.name,
            invesiloters: [
            ],
            studentCount: studentCount
          }
          this.selectedRoomsForInvesiloter.push(currentDetials);
        }
      }
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
  reAssignSeats() {
    this.assignSeats();
    this.getAssignButtonStatus();
  }
  deleteMemoData() {
    let self = this;
    this.confirmationService.confirm({
      message: `Are you sure you want to delete <b> ${self.titleOfExam} </b>?`,
      accept: () => {
        self.memoService.delete(self.titleOfExam).subscribe(res => {
          self.notificationService.showSuccess('Successfully deleted', 'Memo');
          self.titleOfExam = '';
        });
      }
    });
  }
  getMispell(roomId){
    let id = roomId.id;
    if("Invesiloters" == id){
      return "Invigilator";
    } else {
      return id + "("+ roomId.studentCount + ")";
    }
  }
}
