import { Injectable } from '@angular/core';
import { MemoDetail } from '../model/report-model';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class MemoService {
  

  path = 'memo'
  constructor(private apiService : ApiService) { }

  getAll() {
    return this.apiService.get(this.path);
  }

  export(selectedMemo: MemoDetail) {
    return this.apiService.postForBlob(this.path ,selectedMemo);
  }

  delete(titleOfExam: string) {
    return this.apiService.delete(`${this.path+'/'+titleOfExam}`);
  }
}
