import {Component, Input, OnInit} from '@angular/core';
import {User} from '../../core/user/user.model';
import {ParticipantService} from '../../admin/participants/participant.service';
import {Participant} from '../../admin/participants/participant.model';

@Component({
  selector: 'cf-participation-status',
  templateUrl: './participation-status.component.html',
  styleUrls: ['./participation-status.component.css']
})
export class ParticipationStatusComponent implements OnInit {

  @Input()
  model: User;
  data: Participant;

  constructor(private service: ParticipantService) {
  }

  ngOnInit() {
    this.service.getByUser(this.model.id)
      .flatMap(it => this.service.addVoucher(it))
      .subscribe(it => this.data = it);
  }

}
