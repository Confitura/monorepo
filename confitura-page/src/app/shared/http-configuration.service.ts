import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';

@Injectable()
export class HttpConfiguration {
  public apiServer: String;

  constructor() {
    this.apiServer = environment.API_URL;
  }

}
