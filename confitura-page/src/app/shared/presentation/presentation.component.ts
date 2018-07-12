import {Component, Input, OnInit} from '@angular/core';
import {DescriptionType, Presentation} from '../../profile/shared/presentation.model';
import {PresentationService} from '../../profile/shared/presentation.service';
import {UserService} from '../../core/user/user.service';
import {PersonModalService} from '../person-modal/person-modal.service';
import {User} from '../../core/user/user.model';
import {VoteStats} from '../../admin/votes/vote-list/vote-stats.model';

@Component({
  selector: 'cf-presentation',
  templateUrl: './presentation.component.html',
  styleUrls: ['./presentation.component.scss']
})
export class PresentationComponent implements OnInit {
  @Input()
  presentation: Presentation;
  @Input()
  voteStats: VoteStats;
  @Input()
  descriptionType: DescriptionType = DescriptionType.Both;

  descriptionTypes = DescriptionType;


  constructor(private service: PresentationService,
              private userService: UserService,
              private personModalService: PersonModalService) {
  }

  ngOnInit() {
  }

  accept(presentation: Presentation) {
    this.service.accept(presentation)
      .subscribe(() => this.ngOnInit());
  }

  unaccept(presentation: Presentation) {
    this.service.unaccept(presentation)
      .subscribe(() => this.ngOnInit());
  }

  show(speaker: User) {
    this.userService.getBy(speaker.id)
      .subscribe(user => this.personModalService.showFor(user));
  }

  isAccepted() {
    return this.presentation.status === 'accepted';
  }

  allSpeakersFor(presentation: Presentation): User[] {
    return [presentation.speaker, ...presentation.cospeakers];
  }

  shouldShowDescription(type: DescriptionType) {
    return this.descriptionType === DescriptionType.Both || this.descriptionType === type;
  }

}
