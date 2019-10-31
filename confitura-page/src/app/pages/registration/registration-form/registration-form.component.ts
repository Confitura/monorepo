import {Component, OnInit} from '@angular/core';
import {Participant} from '../../../admin/participants/participant.model';
import {ParticipantService} from '../../../admin/participants/participant.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable, of, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Voucher} from '../../../admin/vouchers/voucher.model';
import {Location} from '@angular/common';
import {CurrentUser} from '../../../core/security/current-user.service';
import {VouchersService} from '../../../admin/vouchers/vouchers.service';
import {ValidationErrors} from '@angular/forms/src/directives/validators';
import {AbstractControl} from '@angular/forms/src/model';
import {UserService} from '../../../core/user/user.service';

@Component({
  templateUrl: './registration-form.component.html',
  styleUrls: ['./registration-form.component.scss'],
  providers: [VouchersService]
})
export class RegistrationFormComponent implements OnInit {
  submitted = false;
  error: string;

  registrationForm: FormGroup;
  private readonly id: string;

  constructor(private service: ParticipantService,
              private voucherService: VouchersService,
              private route: ActivatedRoute,
              private router: Router,
              private formBuilder: FormBuilder,
              private currentUser: CurrentUser,
              private userService: UserService,
              private location: Location) {

    this.registrationForm = formBuilder.group({
      voucher: [null, null, this.voucherValid.bind(this)],
      sex: [null, Validators.required],
      size: [null, Validators.required],
      mealOption: [null, Validators.required],
      city: [null, Validators.required],
      experience: [null, Validators.required],
      role: [null, Validators.required]
    });

    this.id = this.route.snapshot.params['id'];
    const voucher = this.route.snapshot.params['voucher'] || '';
    if (this.id) {
      this.service.getOne(this.id)
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
          this.registrationForm.get('sex').disable();
          this.registrationForm.get('size').disable();
          this.registrationForm.get('mealOption').disable();
          this.registrationForm.get('city').disable();
          this.registrationForm.get('experience').disable();
          this.registrationForm.get('role').disable();
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
            return throwError(error);
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

  voucherValid(control: AbstractControl): Observable<ValidationErrors> {
    if (!control.value) {
      return of(null);
    }
    return this.voucherService.check(control.value)
      .pipe(
        map(() => null),
        catchError(error => {
          console.error(error);
          if (error.error === 'TAKEN') {
            return of({voucherTaken: true, voucherInvalid: true});
          } else {
            return of({voucherTaken: false, voucherInvalid: true});
          }
        }));
  }

  ngOnInit(): void {
    this.userService.getBy(this.currentUser.get().jti)
      .subscribe(() => console.log('loaded'));
  }
}
