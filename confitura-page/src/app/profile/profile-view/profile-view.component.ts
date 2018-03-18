import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Presentation} from '../shared/presentation.model';
import {PresentationService} from '../shared/presentation.service';
import {ActivatedRoute, Params} from '@angular/router';
import {ConfirmationService} from '../../core/confirmation.service';
import {User} from '../../core/user/user.model';
import {UserService} from '../../core/user/user.service';
import {Location} from '@angular/common';

@Component({
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.scss']
})
export class ProfileViewComponent implements OnInit {

  model: User;
  presentations: Observable<Presentation[]>;

  constructor(private service: UserService,
              private presentationService: PresentationService,
              private route: ActivatedRoute,
              private confirmation: ConfirmationService) {

  }

  ngOnInit(): void {
    this.reload();
  }

  reload() {
    this.route.params
      .subscribe((params: Params) => {
        const id = params['id'];
        if (id) {
          this.service.getBy(id)
            .subscribe(user => {
              this.model = user;
              user.photo += '?v=' + new Date().getMilliseconds();
            });
          this.presentations = this.presentationService.getAllFor(id);
        }
      });
  }

  remove(presentation: Presentation) {
    this.confirmation.show('you want to delete this presentation?')
      .then(() => this.presentationService.remove(presentation)
        .subscribe(() => this.reload()));
  }

}
