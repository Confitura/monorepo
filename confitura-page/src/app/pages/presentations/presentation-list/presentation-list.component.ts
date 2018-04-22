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
  private EMPTY_FILTER = {
    status: '*',
    language: '*',
    level: '*',
    text: ''
  };
  original: Presentation[];
  list: Presentation[];
  filter = {...this.EMPTY_FILTER};

  constructor(private service: PresentationService,
              private personModalService: PersonModalService,
              private userService: UserService,
              private route: ActivatedRoute) {
    this.doFilterByStatus = this.doFilterByStatus.bind(this);
    this.doFilterByText = this.doFilterByText.bind(this);
    this.doFilterBy = this.doFilterBy.bind(this);
  }

  ngOnInit(): void {
    this.service.getAll()
      .subscribe(list => {
        this.original = list;
        this.doFilter();
        setTimeout(() => this.scrollToSelectedPresentation(), 500);
      });
  }



  clear() {
    this.filter = {...this.EMPTY_FILTER};
    this.doFilter();
  }

  doFilter() {
    console.log('filter', this.filter);
    this.list = this.original
      .filter(this.doFilterByStatus)
      .filter(this.doFilterBy('language'))
      .filter(this.doFilterBy('level'))
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

  doFilterBy(field: string) {
    return (presentation: Presentation) => {
      return this.filter[field] === '*' ||
        this.filter[field] === presentation[field];
    };
  }

  private allSpeakersFor(presentation: Presentation): User[] {
    return [presentation.speaker, ...presentation.cospeakers];
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
