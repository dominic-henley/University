/*
 * COMP10002 Foundations of Algorithms, Semester 1, 2021
 * 
 * Full Name: Dominic Sebastian Henley      
 * Student Number: 1186484
 * Date: 11 May 2021
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>

#define MAX_WORDS 100
#define MAX_CHARS 22
#define MAX_TAGS 5
#define MAX_CHAR_TAGS 4
#define MAX_FORMS 4
#define MAX_CHAR_FORMS 25
#define MAX_CHAR_SNTC 25

/* Code obtained and adapted from listops.c from lecture */
typedef struct node node_t;

struct node{
    char word[MAX_CHAR_SNTC+1];
    node_t *next;
};

typedef struct{
    node_t *head;
    node_t *foot;
} list_t;
/* ===================================================== */

typedef struct{
    char word[MAX_CHARS+1]; /* add 1 for null byte */
    char tags[MAX_TAGS*MAX_CHAR_TAGS+5]; /* add 5 to account for nullbyte + whitespace */
    char forms[MAX_FORMS*MAX_CHAR_FORMS+5]; /* add 5 to account for null byte + numbers */
} word_t;

void print_stage_header(int n);
void fetch_word(word_t *words);
int find_form(char *forms, char* word);

void stage1(word_t *words, int *nwords);
void stage2(word_t *words, int *nforms, int *nwords);
void stage3(word_t *words, list_t *list, int *nwords);
void stage4(word_t *words, list_t *list, int *nwords);

/* Obtained and adapted from lecture and textbook */   
int getword(char W[], int limit); 
list_t *make_empty_list(void);
void free_list(list_t *list);
list_t *insert_at_foot(list_t *list, char word[]);
int cmp(const void *v1, const void *v2);
/* ============================================== */

int main(int argc, char *argv[]){
    word_t words[MAX_WORDS];
    list_t *list = make_empty_list(); 
    int nwords = 0, nforms = 0;

    stage1(words, &nwords);
    stage2(words, &nforms, &nwords);
    stage3(words, list, &nwords);
    stage4(words, list, &nwords);
    /* 
        Explanation of time complexity at the bottom!
    */
   free_list(list);
   list = NULL;
}

// Prints stage header
void print_stage_header(int n){
    int i;
    for(i = 0; i < 26; i++){
        printf("=");
    }
    printf("Stage %d", n);
    for(i = 0; i < 26; i++){
        printf("=");
    }
    printf("\n");
}

// Reads in a line from stdin and stores it in a word_t
void fetch_word(word_t *words){
    /* the '$' character has already been flushed in main() */
    char c;
    int index = 0;
    scanf("%s", words->word);
    getchar(); // flushes '\n'
    while((c = getchar()) != '\n'){
        words->tags[index++] = c;
    }
    getchar(); // flushes '#'
    index = 0;
    while((c = getchar()) != '\n'){
        words->forms[index++] = c;
    }
}

/* Code obtained from getword.c */ 
int getword(char W[], int limit){
	int c, len=0;
	/* first, skip over any non alphabetics */
	while((c=getchar())!=EOF && !isalpha(c)) {
		/* do nothing more */
	}
	if (c==EOF) {
		return EOF;
	}
	/* ok, first character of next word has been found */
	W[len++] = c;
	while (len<limit && (c=getchar())!=EOF && isalpha(c)) {
		/* another character to be stored */
		W[len++] = c;
	}
	/* now close off the string */
	W[len] = '\0';
	return 0;
}
/* ============================= */

/* Code obtained and adapted from listop.c */
list_t *make_empty_list(void) {
	list_t *list;
	list = (list_t*)malloc(sizeof(*list));
	assert(list!=NULL);
	list->head = list->foot = NULL;
	return list;
}

list_t *insert_at_foot(list_t *list, char word[]) {
	node_t *new;
	new = (node_t*)malloc(sizeof(*new));
	assert(list!=NULL && new!=NULL);
	strcpy(new->word, word);
	new->next = NULL;
	if (list->foot==NULL) {
		/* this is the first insertion into the list */
		list->head = list->foot = new;
	} else {
        list->foot->next = new;
        list->foot = new;
    }
	return list;
}

void free_list(list_t *list){
	node_t *curr, *prev;
	assert(list!=NULL);
	curr = list->head;
	while (curr) {
		prev = curr;
		curr = curr->next;
		free(prev);
	}
	free(list);
}
/* ======================================= */

int cmp(const void *v1, const void *v2){
    const word_t *word1 = v1, *word2 = v2;
    return strcmp(word1->word, word2->word);
}

// searches for word in forms, returns 1 if found, else return 0
int find_form(char *forms, char* word){
    char cmp[MAX_CHAR_FORMS];
    int index = 0, i, j;
    while(forms[index] != '\0'){
        if(isdigit(forms[index])){
            j = 1;
            i = 0;
            while(!isdigit(forms[index+j]) && forms[index+j] != '\0'){
                cmp[i++] = forms[index+j];
                j++;
            }
            cmp[i] = '\0';
        }
        index++;
    }
    return !strcmp(word, cmp); // return !strcmp to make return type truthy
}

void stage1(word_t *words, int *nwords){
    char c;
    int index = 0;
    print_stage_header(1);
    while((c = getchar()) != '*'){
        fetch_word(&words[index++]);
        (*nwords)++;
    }
    while((c=getchar()) == '*'); // flushes out line of '*'s
    printf("Word 0: %s\n", words[0].word);
    printf("POS: %s\n", words[0].tags);
    printf("Form: %s\n", words[0].forms);
}

void stage2(word_t *words, int *nforms, int *nwords){
    int i, j, len;
    print_stage_header(2);
    for(i = 0; i < *nwords; i++){
        len = strlen(words[i].forms);
        for(j = 0; j < len; j++){
            if(isdigit(words[i].forms[j])){
                (*nforms)++;
            }
        }
    }
    printf("Number of words: %d\n", *nwords);
    printf("Average number of variation forms per word: %.2f\n", 
            (double)(*nforms) / *nwords);
}

void stage3(word_t *words, list_t *list, int *nwords){
    print_stage_header(3);
    char word[MAX_CHAR_SNTC];
    while(getword(word, MAX_CHAR_SNTC) != EOF){
        insert_at_foot(list, word);
    }
    node_t *curr = list->head;
    while(curr!=NULL){
        printf("%-26s", curr->word);
        word_t *obj = bsearch(curr->word, words, *nwords, sizeof(words[0]), cmp);
        if(obj){
            printf("%s\n", obj->tags);
        }else{
            printf("%s\n", "NOT_FOUND");
        }
        curr = curr->next;
    }
}

void stage4(word_t *words, list_t *list, int *nwords){
    print_stage_header(4);
    int i;
    char *currword, *forms, *root;
    node_t *curr = list->head;
    word_t *ret; 
    while(curr!=NULL){
        printf("%-26s", curr->word);
        for(i = 0; i < *nwords; i++){
            currword = curr->word;
            forms = words[i].forms;
            root = words[i].word;
            // if substring is found or it is already in root form then exit loop
            if(find_form(forms, currword) || strcmp(currword, root) == 0){
                printf("%-26s", root);
                break;
            } else if(i == (*nwords)-1){
                /* if no substring is found and 
                not in root form then simply print word */
                printf("%-26s", currword);
                root = currword; // sets the word we want to bsearch into root
            }
        }
        ret = bsearch(root, words, *nwords, sizeof(words[0]), cmp);
        if(ret){
            printf("%s\n", ret->tags);
        } else {
            printf("%s\n", "NOT_FOUND");
        }
        curr = curr->next;
    }
}
/*  
    Time complexity: O(s*d*f*m*(min{x,m})),
    where x is the length of the word we are searching for.

    My algorithm involves a find_form() function that parses the forms string
    and extracts words from it. It then uses strcmp to compare the word we are 
    looking for. This means that in the worst case, which is when none of the 
    words are found in the dictionary, then my algorithm would look through every
    char in every word in the dictionary, which translates to O(s*d*f*m) operations.
    I also have to account for the strcmp() use, which costs min{x,m}, bringing
    the total time complexity to O(s*d*f*m*(min{x,m})) where x is the length
    of the word we are searching for.
*/