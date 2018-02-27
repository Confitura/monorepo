import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmationService} from '../../core/confirmation.service';
import {PresentationService} from '../shared/presentation.service';
import {ActivatedRoute} from '@angular/router';
import {User} from '../../core/user/user.model';
import {catchError} from 'rxjs/operators';
import {Observable} from 'rxjs/Observable';
import {MatSnackBar} from '@angular/material';
import {FormControl} from '@angular/forms';
import {Location} from '@angular/common';

@Component({
  selector: 'cf-cospeakers',
  templateUrl: './cospeakers.component.html',
  styleUrls: ['./cospeakers.component.scss']
})
export class CospeakersComponent implements OnInit {

  email: string;
  list: User[] = [];
  private presentationId: string;
  @ViewChild('form') form: FormControl;

  constructor(private confirmation: ConfirmationService,
              private service: PresentationService,
              private route: ActivatedRoute,
              private snackBar: MatSnackBar,
              private location: Location) {
  }

  ngOnInit() {
    this.presentationId = this.route.snapshot.params['id'];
    this.service.getCospeakers(this.presentationId)
      .subscribe((speakers) => this.list = speakers);
  }

  add() {
    this.service.addCospeaker(this.presentationId, this.email)
      .pipe(
        catchError(error => {
          let message = 'Ups! Something went wrong.';
          if (error.status === 404) {
            message = 'Ups! Speaker with given email does not exist in our system';
          }
          this.snackBar.open(message, null, {
            duration: 5000,
          });
          return Observable.throw(error);
        }))
      .subscribe(user => {
        this.list = [...this.list, user];
        this.form.reset();
      });
  }

  remove(speaker: User) {
    this.confirmation.show('Are you sure you want to remove this co-speaker?')
      .then(() => {
        this.service.removeCospeaker(this.presentationId, speaker.email)
          .subscribe(() => this.list = this.list.filter(it => it.id !== speaker.id));
      });
  }

  done() {
    this.location.back();
  }

}
