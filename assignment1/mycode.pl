% COMP3411/COMP9414 Assignment1

%%%%%%%%%%%%%%%%%% Q1 %%%%%%%%%%%%%%%%%%%

% trivial branch
sumsq_even([],0).

% recursive branch
% case1: if the number is even we take the square and sum it up.

sumsq_even([Item | Rest], Sum):-
	0 is Item mod 2,
	sumsq_even( Rest, SumOfRest),
	Sum is Item*Item + SumOfRest.

% case2: otherwise, we do nothing for the odd number.
sumsq_even([Item | Rest], Sum):-
    1 is Item mod 2,
	sumsq_even( Rest, SumOfRest),
	Sum is 0 + SumOfRest.


%%%%%%%%%%%%%%%%%% Q2 %%%%%%%%%%%%%%%%%%%

% To check if two persons have a same family name, the easiest of doing it is to
% check if the two person have the same greatest ancestor. If so they are within
% a same family, otherwise not. To achieve this, we use the same method of the 
% exersice(e.g. ancestor(Ancestor,Person).). We trace the greatest ancestors of 
% these two person and check if their ancestors are the same.

% trivial branch. If Person1 and Person2 are the greastest ancestors, we check whether
% they are the same.

same_name(Person1,Person2):-
    \+ parent(_,Person1),    
    \+ parent(_,Person2),
    Person1 == Person2.

% trace the Ancestor of Person1 until we find the greatest. 
same_name(Person1,Person2):-
	parent(Ancestor1,Person1),
	male(Ancestor1),
	same_name(Ancestor1,Person2).
	
% trace the Ancestor of Person2 until we find the greatest. 
same_name(Person1,Person2):-
	parent(Ancestor2,Person2),
	male(Ancestor2),
	same_name(Person1,Ancestor2).
	
	
%%%%%%%%%%%%%%%%%% Q3 %%%%%%%%%%%%%%%%%%%

% each time we take the head of the list and get the square root
% of that number, then put the [num,square(n)] to the new list.

% trivial branch.
sqrt_list([], []).

% recursive branch
sqrt_list([H|T],[[H,Sqrt]|SqrtRest]):-
        Sqrt is sqrt(H),
        sqrt_list(T,SqrtRest).

%%%%%%%%%%%%%%%%%% Q4 %%%%%%%%%%%%%%%%%%%
% each time we the product of the first two elements, if the product is
% positive we put the first element in the current list, else we start
% a new list.
% there are 6 cases:
%       1. H1*H2 > 0.                       (+)
%       2. H1*H2 = 0, H1 = 0, H2 >= 0.      (+)
%       3. H1*H2 = 0, H1 = 0, H2 < 0.       (-)
%       4. H1*H2 = 0, H2 = 0, H1 >= 0.      (+)
%       5. H1*H2 = 0, H2 = 0, H1 < 0.       (-)
%       6. H1*H2 < 0.                       (-)


% trivial branch.
sign_runs([H], [[H]]).


% recursive branch
sign_runs([H1,H2|T],Result):-
        H1*H2 > 0,
        sign_runs([H2|T], [[TMP_H|TMP_SUB]|TMP_T]),
        Result = [[H1,TMP_H|TMP_SUB]|TMP_T].

sign_runs([H1,H2|T],Result):-
        0 is H1*H2,
        H1 = 0,
        H2 >= 0,
        sign_runs([H2|T], [[TMP_H|TMP_SUB]|TMP_T]),
        Result = [[H1,TMP_H|TMP_SUB]|TMP_T].

sign_runs([H1,H2|T],Result):-
        0 is H1*H2,
        H1 = 0,
        H2 < 0,
        sign_runs([H2|T], [[TMP_H|TMP_SUB]|TMP_T]),
        Result = [[H1],[TMP_H|TMP_SUB]|TMP_T].

sign_runs([H1,H2|T],Result):-
        0 is H1*H2,
        H2 = 0,
        H1 >= 0,
        sign_runs([H2|T], [[TMP_H|TMP_SUB]|TMP_T]),
        Result = [[H1,TMP_H|TMP_SUB]|TMP_T].

sign_runs([H1,H2|T],Result):-
        0 is H1*H2,
        H2 = 0,
        H1 < 0,
        sign_runs([H2|T], [[TMP_H|TMP_SUB]|TMP_T]),
        Result = [[H1],[TMP_H|TMP_SUB]|TMP_T].

sign_runs([H1,H2|T], Result):-
        H1*H2 < 0,
        sign_runs([H2|T], [[TMP_H|TMP_SUB]|TMP_T]),
        Result = [[H1],[TMP_H|TMP_SUB]|TMP_T].


%%%%%%%%%%%%%%%%%% Q5 %%%%%%%%%%%%%%%%%%%

% there are 3 cases for recursive branch:
%       1. a root with a empty left child.
%       2. a root with a empty right child.
%       3. ordinary case.


% trivial branch. The basic case is that the tree is with a root with 
% two empty children.
is_heap(tree(empty,_,empty)).

% recursive branch
is_heap(tree(empty,Root,R)):-
	R = tree(_,RRoot,_),      
	Root =< RRoot,
	is_heap(R).


is_heap(tree(L,Root,empty)):-
        L = tree(_,LRoot,_),
        Root =< LRoot,
        is_heap(L).

is_heap(tree(L,Root,R)):-
        L = tree(_,LRoot,_),
        R = tree(_,RRoot,_),
        Root =< LRoot,
        Root =< RRoot,
        is_heap(L),
        is_heap(R).

