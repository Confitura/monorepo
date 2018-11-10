import {throwError as observableThrowError, Observable} from 'rxjs';
import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from '../../shared/confirmation/confirmation.service';
import {PresentationService} from '../shared/presentation.service';
import {ActivatedRoute} from '@angular/router';
import {User} from '../../core/user/user.model';
import {catchError} from 'rxjs/operators';
import {MatSnackBar} from '@angular/material';
import {FormControl, Validators} from '@angular/forms';
import {Location} from '@angular/common';

@Component({
  selector: 'cf-cospeakers',
  templateUrl: './cospeakers.component.html',
  styleUrls: ['./cospeakers.component.scss']
})
export class CospeakersComponent implements OnInit {

  private presentationId: string;
  private validator: (value: any) => any;
  list: User[] = [];
  emailControl = new FormControl('', [
      Validators.required,
      Validators.email,
    ], () => new Promise(resolve => {
      this.validator = resolve;
    })
  );

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
    if (this.emailControl.invalid) {
      return;
    }
    this.service.addCospeaker(this.presentationId, this.emailControl.value)
      .pipe(
        catchError(error => {
          let message = 'Ups! Something went wrong.';
          if (error.status === 404) {
            message = 'Ups! Speaker with given email does not exist in our system';
          } else if (error.status === 409) {
            message = error.error;
          }
          this.validator({speaker: {value: message}});
          return observableThrowError(error);
        }))
      .subscribe(user => {
        this.list = [...this.list, user];
        this.emailControl.setValue(null);
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
