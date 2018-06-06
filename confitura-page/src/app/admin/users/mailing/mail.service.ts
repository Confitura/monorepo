import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs-compat/add/operator/map';

@Injectable()
export class MailService {

    constructor(private http: HttpClient) {
    }

    getTemplates(): Observable<string[]> {
        return this.http.get('/mailing/templates').map(it => <string[]> it);
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
