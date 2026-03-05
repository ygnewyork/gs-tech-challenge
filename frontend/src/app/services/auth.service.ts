import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse, UserProfile } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/api';
  private loggedIn$ = new BehaviorSubject<boolean>(this.hasToken());
  private userName$ = new BehaviorSubject<string>(this.getStoredName());

  isLoggedIn$ = this.loggedIn$.asObservable();
  userName = this.userName$.asObservable();

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, request).pipe(
      tap(res => this.handleAuth(res))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, request).pipe(
      tap(res => this.handleAuth(res))
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
    this.loggedIn$.next(false);
    this.userName$.next('');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/user/profile`);
  }

  private handleAuth(res: AuthResponse): void {
    localStorage.setItem('token', res.token);
    localStorage.setItem('firstName', res.firstName || '');
    localStorage.setItem('lastName', res.lastName || '');
    this.loggedIn$.next(true);
    const name = this.buildName(res.firstName, res.lastName);
    this.userName$.next(name);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  private getStoredName(): string {
    const first = this.cleanValue(localStorage.getItem('firstName'));
    const last = this.cleanValue(localStorage.getItem('lastName'));
    return this.buildName(first, last);
  }

  private buildName(first: string | null | undefined, last: string | null | undefined): string {
    const f = this.cleanValue(first);
    const l = this.cleanValue(last);
    return [f, l].filter(Boolean).join(' ');
  }

  private cleanValue(val: string | null | undefined): string {
    if (!val || val === 'undefined' || val === 'null') return '';
    return val;
  }
}
