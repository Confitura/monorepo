import {Component, OnInit} from '@angular/core';
import {PresentationService} from '../../../profile/shared/presentation.service';
import {Presentation} from '../../../profile/shared/presentation.model';
import {ActivatedRoute} from '@angular/router';
import {PersonModalService} from '../../../shared/person-modal/person-modal.service';
import {UserService} from '../../../core/user/user.service';
import {User} from '../../../core/user/user.model';

@Component({
  templateUrl: './presentation-list.component.html',
  styleUrls: ['./presentation-list.component.scss']

})
export class PresentationListComponent implements OnInit {

  list: Presentation[];
  filter = {
    status: '*',
    text: ''
  };
  private original: Presentation[];

  constructor(private service: PresentationService,
              private personModalService: PersonModalService,
              private userService: UserService,
              private route: ActivatedRoute) {
    this.doFilterByStatus = this.doFilterByStatus.bind(this);
    this.doFilterByText = this.doFilterByText.bind(this);
  }

  ngOnInit(): void {
    this.service.getAll()
      .subscribe(list => {
        this.original = list;
        this.doFilter();
        setTimeout(() => this.scrollToSelectedPresentation(), 500);
      });
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

  clear() {
    this.filter = {
      status: '*',
      text: ''
    };
    this.doFilter();
  }

  doFilter() {
    console.log('filter', this.filter);
    this.list = this.original
      .filter(this.doFilterByStatus)
      .filter(this.doFilterByText);
  }

  doFilterByText(presentation) {
    return [
      presentation.title,
      presentation.description,
      ...this.allSpeakersFor(presentation).map(it => it.name)
    ]
      .map(it => it.toLowerCase())
      .some(it => it.includes(this.filter.text.toLowerCase()));
  }

  doFilterByStatus(presentation) {
    switch (this.filter.status) {
      case 'a':
        return presentation.status === 'accepted';
      case 'na':
        return presentation.status !== 'accepted';
      default:
        return true;
    }
  }


  private scrollToSelectedPresentation() {
    const presentationId = this.route.snapshot.fragment;
    if (presentationId) {
      const element = document.getElementById(presentationId);
      if (element) {
        window.scrollTo(0, element.offsetTop - 100);
      }
    }
  }
}
