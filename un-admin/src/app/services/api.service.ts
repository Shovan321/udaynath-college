import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http'
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }

  post(url, body: any): any {
    return this.http.post(`${environment.api_url}${url}`, body);
  }

  get(url: string) {
    return this.http.get(`${environment.api_url}${url}`);
  }

  delete(url: string) {
    return this.http.delete(`${environment.api_url}${url}`);
  }

  postFile(path: string, fileList: any) {
    let file: File = fileList[0];
    let formData: FormData = new FormData();
    formData.append('file', file, file.name);
    let headers = new HttpHeaders();
    return this.http.post(`${environment.api_url}${path}`, formData,
      { headers: headers, observe: 'response' });
  }
  postForBlob(url, body: any): any {
    let headers = new HttpHeaders();
    return this.http.post(`${environment.api_url}${url}`, body, { headers: headers, responseType: 'blob', observe: 'response' });
  }
  
}
