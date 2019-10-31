import {combineLatest, from} from 'rxjs';

import {map, mergeMap} from 'rxjs/operators';
import {Component, OnInit} from '@angular/core';
import {PresentationService} from '../../../profile/shared/presentation.service';
import {DescriptionType, Presentation} from '../../../profile/shared/presentation.model';
import {ActivatedRoute} from '@angular/router';
import {PersonModalService} from '../../../shared/person-modal/person-modal.service';
import {UserService} from '../../../core/user/user.service';
import {VoteStatsServiceService} from '../../../admin/votes/vote-list/vote-stats.service';


import {CurrentUser} from '../../../core/security/current-user.service';
import {LikeService} from '../../../shared/presentation/like/like.service';
import * as _ from 'lodash';

@Component({
  templateUrl: './presentation-list.component.html',
  styleUrls: ['./presentation-list.component.scss']

})
export class PresentationListComponent implements OnInit {
  private EMPTY_FILTER = {
    status: 'a',
    language: '*',
    level: '*',
    text: ''
  };
  original: Presentation[];
  list: Presentation[];
  filter = {...this.EMPTY_FILTER};
  statistics = {};

  constructor(private service: PresentationService,
              private personModalService: PersonModalService,
              private userService: UserService,
              private route: ActivatedRoute,
              private voteStatsServiceService: VoteStatsServiceService,
              private currentUser: CurrentUser,
              private likeService: LikeService) {
    this.doFilterByStatus = this.doFilterByStatus.bind(this);
    this.doFilterByText = this.doFilterByText.bind(this);
    this.doFilterBy = this.doFilterBy.bind(this);
    this.getStats();
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
    this.list = this.original
      .filter(this.doFilterByStatus)
      .filter(this.doFilterBy('language'))
      .filter(this.doFilterBy('level'))
      .filter(this.doFilterByText);
  }

  doFilterByText(presentation) {
    return _.flatten([
      presentation.title,
      presentation.description,
      presentation.speakers.map(it => it.name)
    ])
      .filter(it => it)
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

  getDescriptionType() {
    return this.currentUser.isAdmin() ? DescriptionType.Both : DescriptionType.Full;
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

  private getStats() {
    const $likes = this.likeService.getSummary();
    const $voteStats = this.voteStatsServiceService.getAll()
      .pipe(
        mergeMap(it => from(it)));
    combineLatest($voteStats, $likes)
      .pipe(map(([statistics, likes]) => {
        statistics.likes = likes[statistics.presentationId] || 0;
        return statistics;
      }))
      .subscribe(statistics => {
        this.statistics[statistics.presentationId] = statistics;
      });
  }
}
