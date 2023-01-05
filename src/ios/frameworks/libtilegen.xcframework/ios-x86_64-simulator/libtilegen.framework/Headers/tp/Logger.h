
#pragma once

#include <string>

namespace TP {
    class Logger {
        public:
            Logger(const std::string& tag = "Untagged");
            virtual ~Logger();

            void info(const std::string& message) const;
            void warn(const std::string& message) const;
            void error(const std::string& message) const;
            const std::string& getTag(void) const;

            static void setActiveLogger(Logger* logger);
            static Logger* getActiveLogger(void);

            // TODO it would be nice to use Logger as a stream, e.g.
            // getActiveLogger() << "Stuff" << x << std::endl;
            // friend std::ostream& operator<<(std::ostreamos, const std::string& message);

        protected:
            virtual void _info(const std::string& message) const;
            virtual void _warn(const std::string& message) const;
            virtual void _error(const std::string& message) const;

        private:
            std::string $tag;

            static Logger* $activeLogger;
    };
}
