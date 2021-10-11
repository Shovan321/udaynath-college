import { Injectable } from '@angular/core';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  path = 'dashboard';
  constructor(private apiService : ApiService) { }

  getStudentArrangment(body) : any{
    this.postForDocument(`room-arrangement`, body);
  }

  postForDocument(arg0: string, body: any) {
    
    this.apiService.postForBlob(`${this.path}/${arg0}`, body).subscribe(response => {
      var newBlob = new Blob([response.body]);
      if (window.navigator && window.navigator.msSaveOrOpenBlob) {
        window.navigator.msSaveOrOpenBlob(newBlob);
        return;
      }

      var link = document.createElement('a');
      const data = window.URL.createObjectURL(newBlob);
      link.setAttribute("href", data);
      link.download = response.headers.get("file-name");

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(data);
    });
    
  }
}
