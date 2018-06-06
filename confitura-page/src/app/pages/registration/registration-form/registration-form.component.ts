import {Component, ViewChild} from '@angular/core';
import {Participant} from '../../../admin/participants/participant.model';
import {ParticipantService} from '../../../admin/participants/participant.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {catchError} from 'rxjs/operators';
import {Voucher} from '../../../admin/vouchers/voucher.model';

@Component({templateUrl: './registration-form.component.html', styleUrls: ['./registration-form.component.css']})
export class RegistrationFormComponent {
  submitted = false;
  error: string;

  registrationForm: FormGroup;

  constructor(private service: ParticipantService,
              private route: ActivatedRoute,
              private router: Router,
              private formBuilder: FormBuilder) {
    this.registrationForm = formBuilder.group({
      voucher: [null],
      sex: [null, Validators.required],
      size: [null, Validators.required],
      mealOption: [null, Validators.required]
    });

    const id = this.route.snapshot.params['id'];
    const voucher = this.route.snapshot.params['voucher'];
    if (id) {
      this.service.getOne(id)
        .pipe(
          catchError(error => {
            this.error = 'Something went wrong or your token is incorrect.' +
              ' Please try again or contact us at confitura@confitura.pl';
            return Observable.throwError(error);
          })
        )
        .subscribe(participant => {
          this.registrationForm.setValue({
            voucher: participant.voucher.id,
            sex: participant.gender,
            size: participant.size,
            mealOption: participant.mealOption
          });
        });
    } else {
      this.registrationForm.get('voucher').setValue(voucher);
    }

  }

  save() {
    this.submitted = true;
    if (this.registrationForm.valid) {

      const model = new Participant(this.registrationForm.value);
      if (this.registrationForm.value.voucher) {
        model.voucher = new Voucher({id: this.registrationForm.value.voucher});
      }
      this.service.save(model)
        .pipe(
          catchError(error => {
            if (error.error === 'INVALID_VOUCHER') {
              this.error = 'Voucher you provided is invalid';
            } else {
              this.error = 'Something went wrong. Please try again or contact us at confitura@confitura.pl';
            }
            return Observable.throwError(error);
          })
        )
        .subscribe(() => this.router.navigate(['/registration/finish']));
    }
  }

  get voucher() {
    return this.registrationForm.get('voucher');
  }

  get sex() {
    return this.registrationForm.get('sex');
  }

  get size() {
    return this.registrationForm.get('size');
  }

  get mealOption() {
    return this.registrationForm.get('mealOption');
  }
}
