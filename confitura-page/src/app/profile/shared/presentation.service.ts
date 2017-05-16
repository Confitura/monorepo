import {Injectable} from "@angular/core";
import {Presentation} from "./presentation.model";
import {Response} from "@angular/http";
import {Tag} from "./tag.model";
import {Observable} from "rxjs";
import {CustomHttp} from "../../shared/custom-http.service";
import {CurrentUser} from "../../security/current-user.service";
@Injectable()
export class PresentationService {
    constructor(private http: CustomHttp, private user: CurrentUser) {
    }

    save(presentation: Presentation): Observable<Response> {
        return this.http.post(`/users/${this.user.get().jti}/presentations`, presentation);
    }


    allTags() {
        return this.http.get("/tags")
            .map((response: Response) => response.json()["_embedded"]["tags"] as object[])
            .map((objects: object[]) => objects.map(value => new Tag(value["id"], value["name"])));
    }

    getAll(): Observable<Presentation[]> {
        return this.http.get("/presentations?projection=inlineSpeaker")
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
}