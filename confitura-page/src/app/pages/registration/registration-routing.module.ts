import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {RegistrationInfoComponent} from './registration-info/registration-info.component';
import {RegistrationFormComponent} from './registration-form/registration-form.component';
import {IsAuthenticatedGuard} from './is-authenticated-guard.service';

const routes: Routes = [
  {path: 'registration', component: RegistrationInfoComponent},
  {path: 'registration/form', component: RegistrationFormComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'registration/form/:id', component: RegistrationFormComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'registration/:name', component: RegistrationInfoComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  providers: [IsAuthenticatedGuard]

})
export class RegistrationRoutingModule {

}
