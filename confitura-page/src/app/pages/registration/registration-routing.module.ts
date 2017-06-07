import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {RegistrationInfoComponent} from "./registration-info/registration-info.component";
import {RegistrationFormComponent} from "./registration-form/registration-form.component";
const routes: Routes = [
    {path: "registration", component: RegistrationInfoComponent},
    {path: "registration/:name", component: RegistrationInfoComponent},
    {path: "registration/form/:id", component: RegistrationFormComponent},
];
@NgModule({
    imports: [RouterModule.forChild(routes)]

})
export class RegistrationRoutingModule {

}