cmake_minimum_required(VERSION 3.0.0)

project (brotli)

find_package(Java REQUIRED COMPONENTS Development)
find_package(JNI REQUIRED)

if (JNI_FOUND)
    message (STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    message (STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
endif()
include_directories(${JNI_INCLUDE_DIRS})
include_directories(${JNI_LIBRARIES})

SET (BROTLI_INCLUDE_DIRS "brotli/include")
include_directories(${BROTLI_INCLUDE_DIRS})

SET (LIB_TYPE SHARED)
SET (CMAKE_JNI_TARGET TRUE)
SET (CMAKE_CXX_FLAGS "-Wall -O3")

if(WIN32)
    SET(STATIC_LIBRARY_CXX_FLAGS /MD)
    add_compile_options(/EHsc)
    ## doesnt work, why ????
    string(REPLACE "/O2" "/Ox" CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE}")
else(WIN32)
    SET(STATIC_LIBRARY_CXX_FLAGS)
endif()

add_library (brotli ${LIB_TYPE}
                #add_library(brotlicommon
                "brotli/common/dictionary.c"
                #add_library(brotlidec
                "brotli/dec/bit_reader.c"
                "brotli/dec/decode.c"
                "brotli/dec/huffman.c"
                "brotli/dec/state.c"
                #add_library(brotlienc
                "brotli/enc/backward_references.c"
                "brotli/enc/backward_references_hq.c"
                "brotli/enc/bit_cost.c"
                "brotli/enc/block_splitter.c"
                "brotli/enc/brotli_bit_stream.c"
                "brotli/enc/cluster.c"
                "brotli/enc/compress_fragment.c"
                "brotli/enc/compress_fragment_two_pass.c"
                "brotli/enc/dictionary_hash.c"
                "brotli/enc/encode.c"
                "brotli/enc/entropy_encode.c"
                "brotli/enc/histogram.c"
                "brotli/enc/literal_cost.c"
                "brotli/enc/memory.c"
                "brotli/enc/metablock.c"
                "brotli/enc/static_dict.c"
                "brotli/enc/utf8_util.c"
                "jbrotli-native/src/main/cpp/param_converter.cxx"
                "jbrotli-native/src/main/cpp/org_meteogroup_jbrotli_BrotliCompressor.cxx"
                "jbrotli-native/src/main/cpp/org_meteogroup_jbrotli_BrotliStreamCompressor.cxx"
                "jbrotli-native/src/main/cpp/org_meteogroup_jbrotli_BrotliStreamEncoder.cxx"
                # "jbrotli-native/src/main/cpp/org_meteogroup_jbrotli_BrotliStreamDeCompressor.cxx"
                "jbrotli-native/src/main/cpp/org_meteogroup_jbrotli_BrotliDeCompressor.cxx"
                )

SET_TARGET_PROPERTIES (brotli PROPERTIES LINKER_LANGUAGE CXX)