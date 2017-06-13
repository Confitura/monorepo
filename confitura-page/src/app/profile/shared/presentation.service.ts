import {Injectable} from "@angular/core";
import {Presentation} from "./presentation.model";
import {Response} from "@angular/http";
import {Tag} from "./tag.model";
import {Observable} from "rxjs";
import {CustomHttp} from "../../shared/custom-http.service";
import {CurrentUser} from "../../security/current-user.service";
import {User} from "../../pages/profile/user.model";
@Injectable()
export class PresentationService {
    constructor(private http: CustomHttp,
                private user: CurrentUser) {
    }


    save(userId: string, presentation: Presentation): Observable<Response> {
        return this.http.post(`/users/${userId}/presentations`, presentation);
    }


    allTags() {
        return this.http.get("/tags")
            .map((response: Response) => response.json()["_embedded"]["tags"] as object[])
            .map((objects: object[]) => objects.map(value => new Tag(value["id"], value["name"])));
    }

    getAll(): Observable<Presentation[]> {
        let url = "/presentations";
        if (!this.user.isAdmin()) {
            url += "/search/accepted";
        }
        return this.http.get(url + "?projection=inlineSpeaker")
            .map((response: Response) => response.json()["_embedded"]["presentations"] as Presentation[]);

    }

    getAllFor(userId: string): Observable<Presentation[]> {
        return this.http.get(`/users/${userId}/presentations`)
            .map((response: Response) => response.json()["_embedded"]["presentations"] as Presentation[])
    }

    getOne(id: string): Observable<Presentation> {
        return this.http.get(`/presentations/${id}?projection=inlineTags`)
            .map((response: Response) => response.json() as Presentation);
    }

    remove(presentation: Presentation) {
        return this.http.remove(`/presentations/${presentation.id}`)
    }

    getCospeakers(id: string): Observable<User[]> {
        return this.http.get(`/presentations/${id}/cospeakers`)
            .map(response => response.json()["_embedded"]["users"] as User[])
    }

    accept(presentation: Presentation) {
        return this.http.post(`/presentations/${presentation.id}/accept`, {});
    }

    unaccept(presentation: Presentation) {
        return this.http.post(`/presentations/${presentation.id}/unaccept`, {});
    }
}

