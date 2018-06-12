import {Component} from '@angular/core';
import {Participant} from '../../../admin/participants/participant.model';
import {ParticipantService} from '../../../admin/participants/participant.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {catchError} from 'rxjs/operators';
import {Voucher} from '../../../admin/vouchers/voucher.model';
import {Location} from '@angular/common';
import {CurrentUser} from '../../../core/security/current-user.service';

@Component({
  templateUrl: './registration-form.component.html',
  styleUrls: ['./registration-form.component.scss']
})
export class RegistrationFormComponent {
  submitted = false;
  error: string;

  registrationForm: FormGroup;
  private readonly id: string;

  constructor(private service: ParticipantService,
              private route: ActivatedRoute,
              private router: Router,
              private formBuilder: FormBuilder,
              private location: Location,
              private user: CurrentUser) {

    if (!this.route.snapshot.params.id) {
      this.service.getByUser(user.get().jti).subscribe(it =>
        this.router.navigate(['/registration/form/' + it.id, route.snapshot.params])
      );
    }
    this.registrationForm = formBuilder.group({
      voucher: [null],
      sex: [null, Validators.required],
      size: [null, Validators.required],
      mealOption: [null, Validators.required],
      city: [null, Validators.required],
      experience: [null, Validators.required],
      role: [null, Validators.required]
    });

    this.id = this.route.snapshot.params['id'];
    const voucher = this.route.snapshot.params['voucher'];
    if (this.id) {
      this.service.getOne(this.id)
        .pipe(
          catchError(error => {
            this.error = 'Something went wrong or your token is incorrect.' +
              ' Please try again or contact us at confitura@confitura.pl';
            return Observable.throwError(error);
          })
        )
        .subscribe(participant => {
          this.registrationForm.setValue({
            voucher: participant.voucher ? participant.voucher.id : voucher,
            sex: participant.gender,
            size: participant.size,
            mealOption: participant.mealOption,
            city: null,
            experience: null,
            role: null
          });
        });
    } else {
      this.registrationForm.get('voucher').setValue(voucher);
    }

  }

  save() {
    this.submitted = true;
    if (this.registrationForm.valid) {

      const value = this.registrationForm.value;
      const model = new Participant({
        id: this.route.snapshot.params['id'],
        gender: value.sex,
        size: value.size,
        mealOption: value.mealOption,
        voucher: this.createVoucherObject(),
        city: value.city,
        experience: value.experience,
        role: value.role
      });

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

  private createVoucherObject() {
    if (this.registrationForm.value.voucher) {
      return new Voucher({id: this.registrationForm.value.voucher});
    } else {
      return null;
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

  get city() {
    return this.registrationForm.get('city');
  }

  get experience() {
    return this.registrationForm.get('experience');
  }

  get role() {
    return this.registrationForm.get('role');
  }

  cancel() {
    this.location.back();
  }
}
