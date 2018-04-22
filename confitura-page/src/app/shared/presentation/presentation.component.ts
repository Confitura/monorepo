import {Component, Input, OnInit} from '@angular/core';
import {Presentation} from '../../profile/shared/presentation.model';
import {PresentationService} from '../../profile/shared/presentation.service';
import {UserService} from '../../core/user/user.service';
import {PersonModalService} from '../person-modal/person-modal.service';
import {User} from '../../core/user/user.model';

@Component({
  selector: 'cf-presentation',
  templateUrl: './presentation.component.html',
  styleUrls: ['./presentation.component.scss']
})
export class PresentationComponent implements OnInit {
  @Input()
  presentation: Presentation;
  @Input()
  descriptionType: 'both' | 'short' | 'full' = 'both';

  constructor(private service: PresentationService,
              private userService: UserService,
              private personModalService: PersonModalService) {
  }

  ngOnInit() {
  }

  isAccepted(presentation: Presentation) {
    return presentation.status === 'accepted';
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

  allSpeakersFor(presentation: Presentation): User[] {
    return [presentation.speaker, ...presentation.cospeakers];
  }

  shouldShowDescription(type: string) {
    return this.descriptionType === 'both' || this.descriptionType === type;
  }

}
