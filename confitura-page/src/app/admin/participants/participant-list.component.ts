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
  }

  ngOnInit(): void {

    this.service.getAll()
      .subscribe(list => {
        this.list = list;
      });

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
