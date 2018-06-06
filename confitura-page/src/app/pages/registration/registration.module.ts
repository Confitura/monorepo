import {NgModule} from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';  // <-- #1 import module
import {SharedModule} from '../../shared/shared.module';
import {RegistrationInfoComponent} from './registration-info/registration-info.component';
import {RegistrationFormComponent} from './registration-form/registration-form.component';
import {RegistrationRoutingModule} from './registration-routing.module';
import {PagesModule} from '../pages.module';
import {
  MatButtonModule,
  MatChipsModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatRadioModule,
  MatSnackBarModule,
  MatTableModule,
  MatSelectModule
} from '@angular/material';
@NgModule({
  imports: [RegistrationRoutingModule, SharedModule, PagesModule,
    MatButtonModule,
    MatChipsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatRadioModule,
    MatSnackBarModule,
    MatTableModule,
    MatSelectModule,
    ReactiveFormsModule],
  declarations: [RegistrationInfoComponent, RegistrationFormComponent]
})
export class RegistrationModule {

}
