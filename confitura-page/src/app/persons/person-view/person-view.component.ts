import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {User} from '../../pages/profile/user.model';
import {CurrentUser} from '../../security/current-user.service';
import {FileUploader} from 'ng2-file-upload';
import {environment} from '../../../environments/environment';

@Component({
  selector: 'cf-person-view',
  templateUrl: './person-view.component.html',
  styleUrls: ['./person-view.component.scss']

})
export class PersonViewComponent implements OnInit {
  @Input()
  model: User;
  uploader: FileUploader;
  @Output()
  changed: EventEmitter<any> = new EventEmitter();

  constructor(private currentUser: CurrentUser) {

  }

  ngOnInit(): void {
    this.uploader = new FileUploader({
      authToken: this.currentUser.getToken(),
      url: `${environment.API_URL}/resources/${this.model.id}`,
      autoUpload: true,
    });
    this.uploader.onCompleteAll = () => {
      this.changed.emit(null);
    };
  }

  selectNewPhoto() {
    $('input[type=file]').click();
  }
}
