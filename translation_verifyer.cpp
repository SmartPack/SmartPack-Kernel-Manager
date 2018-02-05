#include <cctype>
#include <dirent.h>
#include <iostream>
#include <fstream>
#include <map>
#include <queue>

const char *FILE_NAME = "strings.xml";
const char *XML_STRING_NAME = "name=\"";

void printUsage(char *name) {
    printf("Usage: %s (path to resources)\n", name);
}

void getXMLFiles(char *path, std::vector<char *> *list) {
    DIR *dir = opendir(path);
    if (!dir) return;

    struct dirent *directory;
    while ((directory = readdir(dir)) != nullptr) {
        char *fileName = directory->d_name;
        if (strcmp(fileName, ".") == 0 || strcmp(fileName, "..") == 0) continue;

        if (directory->d_type == DT_DIR) {
            auto *nextPath = (char *) malloc(strlen(path) + strlen(fileName) + 2);

            strcpy(nextPath, path);
            strcat(nextPath, "/");
            strcat(nextPath, fileName);
            getXMLFiles(nextPath, list);

            delete (nextPath);
        } else if (directory->d_type == DT_REG && strcmp(fileName, FILE_NAME) == 0) {
            auto *fullPath = (char *) malloc(strlen(path) + strlen(FILE_NAME) + 2);
            strcpy(fullPath, path);
            strcat(fullPath, "/");
            strcat(fullPath, FILE_NAME);
            list->push_back(fullPath);
        }
    }
    closedir(dir);
}

bool getFormatSpecifier(const char *translation,
                        char **name, std::queue<char> *specifiers) {

    size_t nameLength = strlen(XML_STRING_NAME);
    size_t count = 0;
    bool found = false;

    while (*translation != '\0') {
        if (count == nameLength) {
            found = true;
            break;
        }
        if (*translation == XML_STRING_NAME[count]) {
            count++;
        } else {
            count = 0;
        }
        translation++;
    }

    if (!found) return false;

    count = 0;
    while (*translation != '"' && *translation != '\0') {
        *(*name) = *translation;
        *name += 1;
        translation++;
        count++;
    }
    *(*name) = '\0';

    *name -= count;

    if (*translation != '"') return false;

    while (*translation != '>' && *translation != '\0') {
        translation++;
    }

    if (*translation != '>') return false;

    for (; *translation != '\0'; translation++) {
        if (*translation == '%') {
            translation++;
            if (*translation == '\0') break;
            if (isdigit(*translation)) {
                if (*(++translation) == '$' && isalpha(*(++translation))) {
                    specifiers->push(*translation);
                } else if (*translation == '\0') break;
            } else if (isalpha(*translation)) {
                specifiers->push(*translation);
            }
        }
    }

    return !specifiers->empty();
}

bool getQueueFromMap(char *key,
                     std::map<char *, std::queue<char>> *specifiersMap,
                     std::queue<char> *specifiers) {
    for (auto specifier : *specifiersMap) {
        if (strcmp(specifier.first, key) == 0) {
            *specifiers = specifier.second;
            return true;
        }
    }
    return false;
}

bool checkFormatSpecifiers(
        const char *file,
        const char *translation,
        std::map<char *, std::queue<char>> specifiersMap) {

    bool success = true;
    auto *name = (char *) malloc(100);

    std::queue<char> requiredSpecifiers;
    std::queue<char> specifiers;

    bool hasSpecifier = getFormatSpecifier(translation, &name, &specifiers);
    bool hasQueue = getQueueFromMap(name, &specifiersMap, &requiredSpecifiers);
    if (hasSpecifier && hasQueue) {
        if (requiredSpecifiers.size() == specifiers.size()) {
            while (!requiredSpecifiers.empty()) {
                if (requiredSpecifiers.front() != specifiers.front()) {
                    success = false;
                    printf("%s: %s\n", file, name);
                    break;
                }
                requiredSpecifiers.pop();
                specifiers.pop();
            }
        } else {
            printf("%s: %s\n", file, name);
            success = false;
        }
    } else {
        if (!hasSpecifier && hasQueue) {
            printf("%s: %s\n", file, name);
            success = false;
        }
    }


    delete (name);
    return success;
}

void freeVector(std::vector<char *> files) {
    for (auto &file : files) {
        delete (file);
    }
    files.clear();
}

void freeMap(std::map<char *, std::queue<char>> specifiers) {
    for (auto &specifier : specifiers) {
        delete (specifier.first);
    }
    specifiers.clear();
}

int main(int argc, char **argv) {
    if (argc != 2) {
        printUsage(argv[0]);
        return 1;
    }

    char *path = argv[1];

    DIR *dir = opendir(path);
    if (!dir) {
        printf("Can't open %s\n", path);
        return 1;
    }

    std::vector<char *> files;
    getXMLFiles(path, &files);

    char *defaultTranslation = nullptr;
    for (auto &file : files) {
        if (strstr(file, "/values/") != nullptr) {
            defaultTranslation = file;
            break;
        }
    }

    if (defaultTranslation == nullptr) {
        printf("Can't find default translation\n");
        freeVector(files);
        return 1;
    }

    std::string line;
    std::ifstream reader;
    reader.open(defaultTranslation);
    if (!reader) {
        printf("Can't read default translation\n");
        freeVector(files);
        return 1;
    }

    std::map<char *, std::queue<char>> specifiersMap;
    while (!reader.eof()) {
        getline(reader, line);
        auto *name = (char *) malloc(100);
        std::queue<char> specifiers;
        bool hasSpecifier = getFormatSpecifier(line.c_str(), &name, &specifiers);

        if (hasSpecifier) {
            specifiersMap.insert(std::pair<char *, std::queue<char>>(name, specifiers));
        } else {
            delete (name);
        }
    }
    reader.close();

    int errorCount = 0;
    for (auto file : files) {
        if (strstr(file, "/values/")) continue;
        reader.open(file);

        while (!reader.eof()) {
            getline(reader, line);
            bool success = checkFormatSpecifiers(file, line.c_str(), specifiersMap);
            if (!success) errorCount++;
        }

        reader.close();
    }

    if (errorCount == 0) {
        printf("No errors found!\n");
    }

    freeMap(specifiersMap);
    freeVector(files);

    return errorCount == 0 ? 0 : 1;
}
