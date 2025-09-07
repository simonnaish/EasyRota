import {HttpClient} from "@angular/common/http";
import {inject, Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {RegistrationRequest, RegistrationResponse} from "src/app/models/registration";

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private readonly apiUrl = 'http://localhost:8080/api/auth';
  private readonly _http: HttpClient = inject(HttpClient);

  constructor() {
  }

  public register(req: RegistrationRequest): Observable<RegistrationResponse> {
    return this._http.post<RegistrationResponse>(`${this.apiUrl}/register`, req);
  }
}

