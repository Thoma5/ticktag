import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
// import { Response, ResponseContentType } from '@angular/http';
import { ApiCallService } from '../../service';
import { Observable } from 'rxjs';
import * as moment from 'moment';

export class TicketResultJson {
  constructor(
    public number: number, public createTime: moment.Moment, public title: string,
    public description: string, public open: boolean, public storyPoints: number,
    public initialEstimatedTime: moment.Duration, public currentEstimatedTime: moment.Duration,
    public dueDate: moment.Moment,
  ) { }
}

export class Comment {
  constructor(
    public createTime: moment.Moment, public text: string,
    public name: string,
  ) { }
}


class MockTicketApi {
  private ticket: TicketResultJson;
  private comments: [Comment];

  constructor() {
    this.ticket = {
      number: 123,
      createTime: moment('1932-01-01 11:12:13'),
      title: 'Break the Enigma',
      /* tslint:disable */
      description: `
# The Engima
![Enigma](https://upload.wikimedia.org/wikipedia/commons/b/bd/Enigma_%28crittografia%29_-_Museo_scienza_e_tecnologia_Milano.jpg)
## History
The Enigma machines were a series of electro-mechanical rotor cipher machines developed and used in the early- to mid-twentieth century to protect commercial, diplomatic and military communication. Enigma was invented by the German engineer Arthur Scherbius at the end of World War I.[1] Early models were used commercially from the early 1920s, and adopted by military and government services of several countries, most notably Nazi Germany before and during World War II.[2] Several different Enigma models were produced, but the German military models are the most commonly recognised. However, Japanese and Italian models have been used

[Wikipedia](https://en.wikipedia.org/wiki/Enigma_machine)

## What we know
* Electrical
* Multiple rotors
  * Rotors create a circuit that scrambles the input

## Why we should break it?
~~It's fun~~
**Used to encrypt majority of german communications**

## The plan
(this should be subtickets)
1. Find out how it works
2. Break it
3. Victory!

## Haskell implementation
\`\`\`haskell
import Char
import List
import Maybe

-- ========== GENERIC FUNCTIONS ========== --
-- Simple piping function
a |> f = f a

-- Function to replace all occurences of a
-- certain item in a list with another item.
replace :: Eq a => a -> a -> [a] -> [a]
replace a b [] = []
replace a b (x:xs)
    | x == a    = b:(replace a b xs)
    | otherwise = x:(replace a b xs)

-- Convert a Char to Int where 'A' = 0
ordA0 :: Char -> Int
ordA0 ch = ord ch - ord 'A'

-- Convert an Int to Char where 'A' = 0
chrA0 :: Int -> Char
chrA0 i = chr (i + ord 'A')

-- Find the index of an element in a list without using Maybe.
idx :: Eq a => a -> [a] -> Int
idx a b = case (elemIndex a b) of (Just i) -> i

-- Rolls a char either left or right.
rollCh :: Int -> Char -> Char
rollCh n ch
    | n < 0     = rollCh (n+26) ch
    | otherwise = ch |> ordA0 |> (+n) |> flip mod 26 |> chrA0

-- Rolls a list of Char.
rollStr :: Int -> String -> String
rollStr 0 str = str
rollStr n (x:xs) = rollStr (n-1) (xs ++ [x])

-- ================================================================== --

-- All Enigma rotors.  Standard rotors are (rotor chars, notch, offset, display)
data Rotor = Rotor (String, Char, Int, Char) | Flat String
    deriving (Show)

type Reflector = Rotor
type Plugboard = Rotor

-- Enigma machine contains a plugboard, 
data Enigma = Enigma (Plugboard, [Rotor], Reflector)
--    deriving (Show)
instance Show Enigma where
    show (Enigma (plugboard, rotors, reflector)) = 
        "\nEnigma: \n"
        ++ "    Plugboard    : " ++ (show plugboard) ++ "\n"
        ++ "    Left Rotor   : " ++ (show (rotors !! 0)) ++ "\n"
        ++ "    Middle Rotor : " ++ (show (rotors !! 1)) ++ "\n"
        ++ "    Right Rotor  : " ++ (show (rotors !! 2)) ++ "\n"
        ++ "    Plugboard    : " ++ (show reflector) ++ "\n\n"


-- Rolls the rotor by n
rollRotor :: Int -> Rotor -> Rotor
rollRotor n (Rotor (str, notch, off, disp)) =  Rotor (newStr, notch, off, newDisp)
    where newStr = rollStr n str
          newDisp = rollCh n disp

-- Do a forward char mapping through a rotor.
mapCharFwd :: Rotor -> Char -> Char
mapCharFwd (Rotor (str, notch, off, disp)) ch = ch
    -- Get char at ch position on the rotor
    |> ordA0 |> (str !!)
    -- Subtract the display char and offset
    |> rollCh (26 - (ordA0 disp - (off-1)))
mapCharFwd (Flat str) ch = ordA0 ch |> (str !!)

-- Do a backward char mapping through a rotor.
mapCharBkw :: Rotor -> Char -> Char
mapCharBkw (Rotor (str, notch, off, disp)) ch = ch
    -- Add the display char
    |> rollCh (ordA0 disp - (off-1))
    -- Find index of that char on the rotor
    |> flip idx str |> chrA0
mapCharBkw (Flat str) ch = ch |> flip idx str |> chrA0

-- Steps a rotor and also returns of the step notch has been hit.
stepRotor :: Rotor -> (Rotor, Bool)
stepRotor rotor@(Rotor (_, notch, _, display))
    | display == notch = (rollRotor 1 rotor, True)
    | otherwise        = (rollRotor 1 rotor, False)

{- Odometer style rotor stepping.
stepRotors :: [Rotor] -> [Rotor]
stepRotors [rotor] = [stepRotor rotor |> fst]
stepRotors rotors = case stepRotor (last rotors) of
    (rotor, True)  -> (stepRotors (init rotors)) ++ [rotor]
    (rotor, False) -> (init rotors) ++ [rotor]
-}

--   .     .
-- BPV -> BQW -> CRX -> CRY
-- Special rotor stepping with hack for double stepping.
stepRotors :: [Rotor] -> [Rotor]
stepRotors [r0, r1, r2] = case stepRotor (r2) of
    (newR2, False) -> case stepRotor (r1) of
        (newR1, False) -> [r0] ++ [r1] ++ [newR2]
        (newR1, True)  -> case stepRotor (r0) of
            (newR0, _) -> [newR0] ++ [newR1] ++ [newR2]
    (newR2, True)  -> case stepRotor (r1) of
        (newR1, False) -> [r0] ++ [newR1] ++ [newR2]
        (newR1, True)  -> case stepRotor (r0) of
                (newR0, _) -> [newR0] ++ [newR1] ++ [newR2]

-- Steps the Enigma machine by one.
stepEnigma :: Enigma -> Enigma
stepEnigma (Enigma (pb, rotors, rf)) = Enigma (pb, stepRotors rotors, rf)

-- Steps the enigma machine by n.
stepEnigmaN :: Int -> Enigma -> Enigma
stepEnigmaN 0 enigma = enigma
stepEnigmaN n (Enigma (pb, rotors, rf)) = stepEnigmaN (n-1) (Enigma (pb, stepRotors rotors, rf))

-- Using an Enigma, process a single char.
cryptCh :: Enigma -> Char -> Char
cryptCh (Enigma (plugboard, rotors, reflector)) ch = 
    ch
        |> mapCharFwd plugboard
        |> mapCharFwd (rotors !! 2)
        |> mapCharFwd (rotors !! 1)
        |> mapCharFwd (rotors !! 0)
        |> mapCharFwd reflector
        |> mapCharBkw (rotors !! 0)
        |> mapCharBkw (rotors !! 1)
        |> mapCharBkw (rotors !! 2)
        |> mapCharBkw plugboard

-- Using an Enigma, process a string.
cryptStr :: Enigma -> String -> String
cryptStr enigma [] = []
cryptStr enigma (x:xs) = (cryptCh steppedEnigma x):(cryptStr steppedEnigma xs)
    where steppedEnigma = stepEnigma enigma

cryptStrMem :: Enigma -> String -> [(Enigma, Char)]
cryptStrMem enigma [] = []
cryptStrMem enigma (x:xs) = (steppedEnigma, cryptCh steppedEnigma x):(cryptStrMem steppedEnigma xs)
    where steppedEnigma = stepEnigma enigma

-- Initializes a rotor based on ring setting and message.
initRotor :: String -> Char -> Int -> Char -> Rotor
initRotor str notch off ch = Rotor (newStr, notch, off, ch)
    where newStr = rollStr (ordA0 ch + 27 - off) str

-- ROTORS             ABCDEFGHIJKLMNOPQRSTUVWXYZ
rotor1   = initRotor "EKMFLGDQVZNTOWYHXUSPAIBRCJ" 'Q'
rotor2   = initRotor "AJDKSIRUXBLHWTMCQGZNPYFVOE" 'E'
rotor3   = initRotor "BDFHJLCPRTXVZNYEIWGAKMUSQO" 'V'
rotor4   = initRotor "ESOVPZJAYQUIRHXLNFTGKDCMWB" 'J'
rotor5   = initRotor "VZBRGITYUPSDNHLXAWMJQOFECK" 'Z'
rotor6   = initRotor "JPGVOUMFYQBENHZRDKASXLICTW" 'Z'
rotor8   = initRotor "FKQHTLXOCBJSPDZRAMEWNIUYGV" 'Z'
rotor7   = initRotor "NZJHGRCXMYSWBOUFAIVLPEKQDT" 'Z'
-- REFLECTORS       ABCDEFGHIJKLMNOPQRSTUVWXYZ
reflectorA  = Flat "EJMZALYXVBWFCRQUONTSPIKHGD"
reflectorB  = Flat "YRUHQSLDPXNGOKMIEBFZCWVJAT"
reflectorC  = Flat "FVPJIAOYEDRZXWGCTKUQSBNMHL"
-- Plugboard: EZ RW MV IU BL PX JO
plugboard  = Flat "ALCDZFGHUOKBVNJXQWSTIMRPYE";
\`\`\`
      `,
      /* tslint:enable */
      open: true,
      storyPoints: 8,
      initialEstimatedTime: moment.duration(4, 'months'),
      currentEstimatedTime: moment.duration(1, 'year'),
      dueDate: moment('1945-05-01 13:14:15'),
    };

    let c1 = {
      createTime: moment('1932-02-12 16:17:18'),
      text: 'Oh well, that wasn\'t so hard',
      name: 'Marian Rejewski',
    };
    let c2 = {
      createTime: moment('1940-01-17 19:20:21'),
      text: 'The new versions are more complex, maybe we can we automate it?',
      name: 'Alan Turing',
    };
    let c3 = {
      createTime: moment('2016-11-20 00:40:42'),
      text: 'Guys, check out this cool sculpture of me!' +
      '![Turing](http://67.media.tumblr.com/1cd835f9cd0bc2cbb8bfa3eb9826956e/tumblr_nvgk08NP7L1rlaql2o7_1280.jpg)',
      name: 'Alan Turing',
    };

    this.comments = [c1, c2, c3];
  }

  getTicket(): Observable<TicketResultJson> {
    return Observable.of(this.ticket)
      .delay(500);
  }

  getComments(): Observable<Comment[]> {
    return Observable.of(this.comments)
      .delay(1000);
  }

  getTicketAndComments(): Observable<[TicketResultJson, Comment[]]> {
    return this.getTicket().zip(this.getComments());
  }
}


@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
})
export class TicketDetailComponent implements OnInit {
  private ticketApi: MockTicketApi;
  private loading = false;
  private ticket: TicketResultJson | null;
  private comments: Comment[] | null;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService) {
    this.ticketApi = new MockTicketApi();
  }

  ngOnInit(): void {
    this.route.params
      .switchMap((params: Params) => {
        // let projectId = +params['projectId'];
        // let ticketNumber = +params['ticketNumber'];

        this.loading = true;
        return this.ticketApi.getTicketAndComments();
      })
      .subscribe(
      result => {
        this.ticket = result[0];
        this.comments = result[1];
        this.loading = false;
      },
      () => { this.loading = false; });
  }
}
