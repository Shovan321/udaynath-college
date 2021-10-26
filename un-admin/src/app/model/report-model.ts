export class MemoDetail {
    id: number;
    name: string;
    reportDTO: ReportDTO;
}
export class ReportDTO {
    title: string;
    selectedRooms: any;
    selectedRoomsForInvesiloter: any;
}
export class MemoReportModel {
    memoModels : MemoDetail[]
}