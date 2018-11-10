import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable()
export class MailService {

  constructor(private http: HttpClient) {
  }

  getTemplates(): Observable<string[]> {
    return this.http.get<string[]>('/mailing/templates');
  }

  sendMail(template: string, info: MessageInfo[]) {
    return this.http.post('/mailing', {
      template: template,
      messageInfoList: info
    });
  }
}

export interface MessageInfo {
  email: string;
  variables?: {};
}
