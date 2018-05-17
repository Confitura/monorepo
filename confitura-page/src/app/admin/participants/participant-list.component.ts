import {Component, OnInit} from '@angular/core';
import {ParticipantService} from './participant.service';
import {Participant} from './participant.model';
import {FileUploader} from 'ng2-file-upload';
import {CurrentUser} from '../../core/security/current-user.service';
import {ConfirmationService} from '../../core/confirmation.service';
import {environment} from '../../../environments/environment';

@Component({
  templateUrl: './participant-list.component.html'
})
export class ParticipantListComponent implements OnInit {


  list: Participant[];
  uploader: FileUploader;
  uploadResponse;

  constructor(private service: ParticipantService,
              private user: CurrentUser,
              private confirmation: ConfirmationService) {
    this.uploader = new FileUploader({
      authToken: this.user.getToken(),
      url: `${environment.API_URL}participants/upload`,
      autoUpload: true,
    });

    this.uploader.onBeforeUploadItem = () => this.uploadResponse = null;
    this.uploader.onSuccessItem = (item, response) => this.showResponse(response);
    this.uploader.onCompleteAll = () => this.ngOnInit();
  }

  ngOnInit(): void {

    this.service.getAll()
      .subscribe(list => {
        this.list = list;
      });

  }

  upload() {
    $('input[type=file]').click();
  }

  sendReminder() {
    this.confirmation.show('you want to send reminder emails to all unregistered participants?')
      .then(() => this.service.sendReminder().subscribe());
  }

  sendTickets() {
    this.confirmation.show('you want to send tickets to participants which haven\'t received  one yet?')
      .then(() => this.service.sendTickets().subscribe());
  }

  sendSurveys() {
    this.confirmation.show('you want to send surveys to all attendees?')
      .then(() => this.service.sendSurveys().subscribe());
  }

  private showResponse(responseString: string) {
    this.uploadResponse = JSON.parse(responseString);
  }
}
